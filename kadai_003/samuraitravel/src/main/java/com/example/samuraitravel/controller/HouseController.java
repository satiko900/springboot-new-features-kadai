package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;        
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    
    public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository, FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;  
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,  
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
        Page<House> housePage;
                
        if (keyword != null && !keyword.isEmpty()) {
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
             } else {
                 housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
             }         
        } else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
        } else if (price != null) {
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
             } else {
                 housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
             }         
        } else {
        	  if (order != null && order.equals("priceAsc")) {
                  housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
              } else {
                  housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
              }           
        }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price); 
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.DESC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        House house = houseRepository.getReferenceById(id);
        Page<Review> reviewPage = reviewRepository.findByHouseId(id, pageable);
        List<Review> reviews = reviewRepository.findByHouseId(id);
        
        model.addAttribute("house", house);  
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviews", reviews);
        
        if (userDetailsImpl != null) {
            Integer myId = userDetailsImpl.getUser().getId();
            model.addAttribute("myId", myId);
            
            Review myreview = reviewRepository.findByHouseIdAndUserId(id, myId);
            model.addAttribute("myreview", myreview);
            
            Favorite myfavorite = favoriteRepository.findByHouseIdAndUserId(id, myId);
            model.addAttribute("myfavorite", myfavorite);
        }
        
        return "houses/show";
    }
        
        @GetMapping("/{id}/create")
        public String create(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        	Favorite favorite = new Favorite();
        	House house = houseRepository.getReferenceById(id);
        	
            
            	User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
            	
                favorite.setHouse(house);
                favorite.setUser(user);  
                favoriteRepository.save(favorite);
            
            
            return "redirect:/houses/{id}";
        	  
    } 
        
        @GetMapping("/{id}/delete")
        public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        	
        	Integer myId = userDetailsImpl.getUser().getId();
        	
        	Favorite myfavorite = favoriteRepository.findByHouseIdAndUserId(id, myId);
        	
        	favoriteRepository.deleteById(myfavorite.getId());
        	
        	return "redirect:/houses/{id}";
        }
    
    
}