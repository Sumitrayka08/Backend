package com.agrinexus.controller;

import com.agrinexus.entity.Crop;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final UserRepository userRepository;

    public CropController(CropService cropService, UserRepository userRepository) {
        this.cropService = cropService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addCrop(Authentication authentication,
                                     @RequestParam(required = false) Long farmId,
                                     @RequestBody Crop crop) {
        User user = getCurrentUser(authentication);
        Long targetFarmId = farmId;
        if (targetFarmId == null && crop.getFarm() != null) {
            targetFarmId = crop.getFarm().getId();
        }
        if (targetFarmId == null) {
            throw new IllegalArgumentException("farmId parameter or farm object with id is required");
        }

        Crop savedCrop = cropService.addCrop(user, targetFarmId, crop);
        return ResponseEntity.ok(savedCrop);
    }

    @GetMapping
    public ResponseEntity<?> getCrops(Authentication authentication,
                                      @RequestParam(required = false) Long farmId) {
        User user = getCurrentUser(authentication);
        List<Crop> crops;
        if (farmId != null) {
            crops = cropService.getCropsByFarm(farmId, user);
        } else {
            crops = cropService.getCropsByUser(user);
        }
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<?> getCropsByFarmId(Authentication authentication, @PathVariable Long farmId) {
        User user = getCurrentUser(authentication);
        List<Crop> crops = cropService.getCropsByFarm(farmId, user);
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCrop(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        Crop crop = cropService.getCropByIdAndUser(id, user);
        return ResponseEntity.ok(crop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCrop(Authentication authentication, @PathVariable Long id, @RequestBody Crop crop) {
        User user = getCurrentUser(authentication);
        Crop updatedCrop = cropService.updateCrop(id, user, crop);
        return ResponseEntity.ok(updatedCrop);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCrop(Authentication authentication, @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        cropService.deleteCrop(id, user);
        return ResponseEntity.ok(Map.of("message", "Crop deleted successfully", "id", id));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}