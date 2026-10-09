package com.agrinexus.controller;

import com.agrinexus.entity.IrrigationInformation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.IrrigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irrigation")
public class IrrigationController {

    private final IrrigationService irrigationService;
    private final UserRepository userRepository;

    public IrrigationController(IrrigationService irrigationService, UserRepository userRepository) {
        this.irrigationService = irrigationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveIrrigation(Authentication authentication,
                                             @RequestParam(required = false) Long farmId,
                                             @RequestBody IrrigationInformation info) {
        User user = getCurrentUser(authentication);
        Long targetFarmId = farmId;
        if (targetFarmId == null && info.getFarm() != null) {
            targetFarmId = info.getFarm().getId();
        }
        if (targetFarmId == null) {
            throw new IllegalArgumentException("farmId parameter or farm object with id is required");
        }

        IrrigationInformation saved = irrigationService.saveIrrigation(user, targetFarmId, info);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<?> getIrrigationByFarm(Authentication authentication, @PathVariable Long farmId) {
        User user = getCurrentUser(authentication);
        List<IrrigationInformation> list = irrigationService.getIrrigationByFarm(farmId, user);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIrrigation(Authentication authentication,
                                               @PathVariable Long id,
                                               @RequestBody IrrigationInformation info) {
        User user = getCurrentUser(authentication);
        IrrigationInformation updated = irrigationService.updateIrrigation(id, user, info);
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

