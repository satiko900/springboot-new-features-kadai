package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	Page<Favorite> findByUserId(Integer userId, Pageable pageable);
	Favorite findByHouseIdAndUserId(Integer houseId, Integer userId);
}
