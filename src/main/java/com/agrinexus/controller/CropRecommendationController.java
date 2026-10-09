package com.agrinexus.controller;

import com.agrinexus.dto.CropRecommendationRequest;
import com.agrinexus.dto.CropRecommendationResponse;
import com.agrinexus.entity.CropRecommendation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.CropRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class CropRecommendationController {

    private final CropRecommendationService recommendationService;
    private final UserRepository userRepository;

    public CropRecommendationController(CropRecommendationService recommendationService, UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<CropRecommendationResponse> recommendCrop(Authentication authentication,
                                                                    @Valid @RequestBody CropRecommendationRequest request) {
        User user = getCurrentUser(authentication);
        CropRecommendationResponse response = recommendationService.processRecommendation(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CropRecommendation>> getHistory(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CropRecommendation> history = recommendationService.getRecommendations(user);
        return ResponseEntity.ok(history);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}