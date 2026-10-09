package com.agrinexus.controller;

import com.agrinexus.entity.SoilInformation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.SoilService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soil")
public class SoilController {

    private final SoilService soilService;
    private final UserRepository userRepository;

    public SoilController(SoilService soilService, UserRepository userRepository) {
        this.soilService = soilService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveSoilInfo(Authentication authentication,
                                          @RequestParam(required = false) Long farmId,
                                          @RequestBody SoilInformation soilInformation) {
        User user = getCurrentUser(authentication);
        Long targetFarmId = farmId;
        if (targetFarmId == null && soilInformation.getFarm() != null) {
            targetFarmId = soilInformation.getFarm().getId();
        }
        if (targetFarmId == null) {
            throw new IllegalArgumentException("farmId parameter or farm object with id is required");
        }

        SoilInformation saved = soilService.saveSoilInfo(user, targetFarmId, soilInformation);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<?> getSoilByFarm(Authentication authentication, @PathVariable Long farmId) {
        User user = getCurrentUser(authentication);
        List<SoilInformation> list = soilService.getSoilByFarm(farmId, user);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSoilInfo(Authentication authentication,
                                            @PathVariable Long id,
                                            @RequestBody SoilInformation soilInformation) {
        User user = getCurrentUser(authentication);
        SoilInformation updated = soilService.updateSoilInfo(id, user, soilInformation);
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

