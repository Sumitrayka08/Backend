package com.agrinexus.controller;

import com.agrinexus.entity.Farm;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.FarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;
    private final UserRepository userRepository;

    public FarmController(FarmService farmService, UserRepository userRepository) {
        this.farmService = farmService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addFarm(Authentication authentication, @RequestBody Farm farm) {
        User user = getCurrentUser(authentication);
        Farm savedFarm = farmService.addFarm(user, farm);
        return ResponseEntity.ok(savedFarm);
    }

    @GetMapping
    public ResponseEntity<?> getMyFarms(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Farm> farms = farmService.getFarmsByUser(user);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFarm(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        Farm farm = farmService.getFarmByIdAndUser(id, user);
        return ResponseEntity.ok(farm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFarm(Authentication authentication, @PathVariable Long id, @RequestBody Farm farm) {
        User user = getCurrentUser(authentication);
        Farm updatedFarm = farmService.updateFarm(id, user, farm);
        return ResponseEntity.ok(updatedFarm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFarm(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        farmService.deleteFarm(id, user);
        return ResponseEntity.ok(Map.of("message", "Farm deleted successfully", "id", id));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}