package com.agrinexus.controller;

import com.agrinexus.entity.FarmerProfile;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.FarmerProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer")
public class FarmerProfileController {

    private final FarmerProfileService farmerProfileService;
    private final UserRepository userRepository;

    public FarmerProfileController(FarmerProfileService farmerProfileService, UserRepository userRepository) {
        this.farmerProfileService = farmerProfileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        FarmerProfile profile = farmerProfileService.getProfile(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", profile.getId());
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("phone", profile.getPhone() != null ? profile.getPhone() : user.getPhone());
        response.put("address", profile.getAddress());
        response.put("state", profile.getState());
        response.put("district", profile.getDistrict());
        response.put("village", profile.getVillage());
        response.put("landArea", profile.getLandArea());
        response.put("preferredLanguage", profile.getPreferredLanguage());
        response.put("location", profile.getLocation());
        response.put("landSize", profile.getLandSize());
        response.put("soilType", profile.getSoilType());
        response.put("preferredCrops", profile.getPreferredCrops());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody FarmerProfileRequest request) {
        User user = getCurrentUser(authentication);

        FarmerProfile profile = farmerProfileService.updateProfile(
                user,
                request.getPhone(),
                request.getAddress(),
                request.getState(),
                request.getDistrict(),
                request.getVillage(),
                request.getLandArea(),
                request.getPreferredLanguage(),
                request.getLocation(),
                request.getLandSize(),
                request.getSoilType(),
                request.getPreferredCrops()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("id", profile.getId());
        response.put("phone", profile.getPhone());
        response.put("address", profile.getAddress());
        response.put("state", profile.getState());
        response.put("district", profile.getDistrict());
        response.put("village", profile.getVillage());
        response.put("landArea", profile.getLandArea());
        response.put("preferredLanguage", profile.getPreferredLanguage());

        return ResponseEntity.ok(response);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public static class FarmerProfileRequest {
        private String phone;
        private String address;
        private String state;
        private String district;
        private String village;
        private Double landArea;
        private String preferredLanguage;
        private String location;
        private String landSize;
        private String soilType;
        private String preferredCrops;

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getVillage() { return village; }
        public void setVillage(String village) { this.village = village; }

        public Double getLandArea() { return landArea; }
        public void setLandArea(Double landArea) { this.landArea = landArea; }

        public String getPreferredLanguage() { return preferredLanguage; }
        public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getLandSize() { return landSize; }
        public void setLandSize(String landSize) { this.landSize = landSize; }

        public String getSoilType() { return soilType; }
        public void setSoilType(String soilType) { this.soilType = soilType; }

        public String getPreferredCrops() { return preferredCrops; }
        public void setPreferredCrops(String preferredCrops) { this.preferredCrops = preferredCrops; }
    }
}