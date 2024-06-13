package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewPostForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final ReviewService reviewService;
	private final UserRepository userRepository;
	
	public ReviewController(ReviewRepository reviewRepository, HouseRepository houseRepository, ReviewService reviewService, UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.reviewService = reviewService;
		this.userRepository = userRepository;
	}
	
	 @GetMapping("/{id}")
     public String index(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		 House house = houseRepository.getReferenceById(id);
		 Page<Review> reviewPage = reviewRepository.findByHouseId(id, pageable);
		 
         model.addAttribute("house", house);
         model.addAttribute("reviewPage", reviewPage);
        
         if (userDetailsImpl != null) {
             Integer myId = userDetailsImpl.getUser().getId();
             model.addAttribute("myId", myId);
         }
         
         return "/reviews/index";
     }   
	 
	 @GetMapping("/{id}/post")
	 public String post(@PathVariable(name = "id") Integer id, Model model) {
		 House house = houseRepository.getReferenceById(id);
		 
		 model.addAttribute("house", house);
		 model.addAttribute("reviewPostForm", new ReviewPostForm());
		 
		 return "/reviews/post";
	 }
	 
	 @PostMapping("/{id}/create")
	 public String create(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @ModelAttribute @Validated ReviewPostForm reviewPostForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		 House house = houseRepository.getReferenceById(id);
		 User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		 
		 reviewPostForm.setHouse(house);
		 reviewPostForm.setUser(user);
		 
		 if (bindingResult.hasErrors()) {
			 return "reviews/post";
		 }
		 
		 reviewService.create(reviewPostForm);
		 redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		 
		 return "redirect:/"; 
	 }
	 
	 @GetMapping("/{id}/{reviewId}/edit")
	 public String edit(@PathVariable(name = "id") Integer id, @PathVariable(name = "reviewId") Integer reviewId, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		 House house = houseRepository.getReferenceById(id);
		 Review review = reviewRepository.getReferenceById(reviewId);
		 ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getHouse(), review.getUser(), review.getEvaluation(), review.getDescription());
		 
		 model.addAttribute("house", house);
		 model.addAttribute("reviewEditForm", reviewEditForm);
		 
		 return "reviews/edit";
	 }
	 
	 @PostMapping("/{id}/{reviewId}/update")
	 public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		 if (bindingResult.hasErrors()) {
			 return "reviews/edit";
		 }
		 
		 reviewService.update(reviewEditForm);
		 redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		 
		 return "redirect:/";
	 }
	 
	 @PostMapping("/{id}/{reviewId}/delete")
	 public String delete(@PathVariable(name = "id") Integer id, @PathVariable(name = "reviewId") Integer reviewId, RedirectAttributes redirectAttributes) {
		 reviewRepository.deleteById(reviewId);
		 
		 redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		 
		 return "redirect:/";
		 
		 
	 }
}
