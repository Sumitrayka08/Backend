package com.agrinexus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrinexus.entity.CropRecommendation;
import com.agrinexus.entity.User;

public interface CropRecommendationRepository
        extends JpaRepository<CropRecommendation, Long> {

    List<CropRecommendation> findByUser(User user);
}