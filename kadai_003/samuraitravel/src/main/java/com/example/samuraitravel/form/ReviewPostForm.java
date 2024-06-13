package com.example.samuraitravel.form;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewPostForm {

	private House house;
	
	
	private User user;
	
	@NotBlank
	private String evaluation;
	
	@NotBlank(message = "コメントを記入してください。")
	private String description;
}
