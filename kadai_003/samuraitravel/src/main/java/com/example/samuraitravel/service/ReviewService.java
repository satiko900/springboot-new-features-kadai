package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewPostForm;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	@Transactional
	public void create(ReviewPostForm reviewPostForm) {
		Review review = new Review();
		
		review.setHouse(reviewPostForm.getHouse());
		review.setUser(reviewPostForm.getUser());
		review.setEvaluation(reviewPostForm.getEvaluation());
		review.setDescription(reviewPostForm.getDescription());
		
		reviewRepository.save(review);
	}
	
	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		review.setHouse(reviewEditForm.getHouse());
		review.setUser(reviewEditForm.getUser());
		review.setEvaluation(reviewEditForm.getEvaluation());
		review.setDescription(reviewEditForm.getDescription());
		
		reviewRepository.save(review);
	}

}
