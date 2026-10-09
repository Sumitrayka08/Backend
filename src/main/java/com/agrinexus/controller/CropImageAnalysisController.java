package com.agrinexus.controller;

import com.agrinexus.entity.CropImageAnalysis;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.CropImageAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/crop-image")
public class CropImageAnalysisController {

    private final CropImageAnalysisService cropImageAnalysisService;
    private final UserRepository userRepository;
    private final String uploadDirectory = "uploads/";

    public CropImageAnalysisController(CropImageAnalysisService cropImageAnalysisService, UserRepository userRepository) {
        this.cropImageAnalysisService = cropImageAnalysisService;
        this.userRepository = userRepository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(Authentication authentication,
                                    @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please upload a valid crop image file"));
        }

        User user = getCurrentUser(authentication);

        try {
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            CropImageAnalysis result = cropImageAnalysisService.analyzeImage(user, originalFileName, filePath);

            return ResponseEntity.ok(Map.of(
                    "id", result.getId(),
                    "imageName", result.getImageName(),
                    "disease", result.getDisease(),
                    "confidence", result.getConfidence(),
                    "recommendation", result.getRecommendation(),
                    "createdAt", result.getCreatedAt().toString()
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Failed to process image file", "error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CropImageAnalysis> history = cropImageAnalysisService.getHistory(user);
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