package com.agrinexus.controller;

import com.agrinexus.entity.ExpertQuery;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.ExpertQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expert/queries")
public class ExpertQueryController {

    private final ExpertQueryService expertQueryService;
    private final UserRepository userRepository;

    public ExpertQueryController(ExpertQueryService expertQueryService, UserRepository userRepository) {
        this.expertQueryService = expertQueryService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createQuery(Authentication authentication, @RequestBody Map<String, String> request) {
        User farmer = getCurrentUser(authentication);
        String question = request.get("question");
        ExpertQuery query = expertQueryService.createQuery(farmer, question);
        return ResponseEntity.ok(query);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExpertQuery>> getMyQueries(Authentication authentication) {
        User farmer = getCurrentUser(authentication);
        return ResponseEntity.ok(expertQueryService.getFarmerQueries(farmer));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ExpertQuery>> getAvailableQueries() {
        return ResponseEntity.ok(expertQueryService.getAvailableQueries());
    }

    @GetMapping
    public ResponseEntity<List<ExpertQuery>> getAllQueries() {
        return ResponseEntity.ok(expertQueryService.getAllQueries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpertQuery> getQueryById(@PathVariable Long id) {
        return ResponseEntity.ok(expertQueryService.getQueryById(id));
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<?> respondToQuery(Authentication authentication,
                                             @PathVariable Long id,
                                             @RequestBody Map<String, String> request) {
        User expert = getCurrentUser(authentication);
        String responseText = request.get("response");
        ExpertQuery updated = expertQueryService.respondToQuery(id, expert, responseText);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        ExpertQuery updated = expertQueryService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}