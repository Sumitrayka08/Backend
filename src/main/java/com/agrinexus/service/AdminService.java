package com.agrinexus.service;

import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;
    private final CropImageAnalysisRepository imageAnalysisRepository;
    private final ExpertQueryRepository queryRepository;
    private final KnowledgeDocumentRepository documentRepository;

    public AdminService(
            UserRepository userRepository,
            FarmRepository farmRepository,
            CropRepository cropRepository,
            CropImageAnalysisRepository imageAnalysisRepository,
            ExpertQueryRepository queryRepository,
            KnowledgeDocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.farmRepository = farmRepository;
        this.cropRepository = cropRepository;
        this.imageAnalysisRepository = imageAnalysisRepository;
        this.queryRepository = queryRepository;
        this.documentRepository = documentRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    public User updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public Map<String, Object> getAnalytics() {
        List<User> allUsers = userRepository.findAll();
        long totalFarmers = allUsers.stream().filter(u -> u.getRole() == Role.FARMER).count();
        long totalExperts = allUsers.stream().filter(u -> u.getRole() == Role.EXPERT).count();
        long totalAdmins = allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count();

        long totalFarms = farmRepository.count();
        long totalCrops = cropRepository.count();
        long totalAnalyses = imageAnalysisRepository.count();
        long totalQueries = queryRepository.count();
        long pendingQueries = queryRepository.findByStatusOrderByCreatedAtDesc("PENDING").size();
        long answeredQueries = queryRepository.findByStatusOrderByCreatedAtDesc("ANSWERED").size();
        long totalKnowledgeDocs = documentRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("totalFarmers", totalFarmers);
        stats.put("totalExperts", totalExperts);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalFarms", totalFarms);
        stats.put("totalCrops", totalCrops);
        stats.put("totalAnalyses", totalAnalyses);
        stats.put("totalQueries", totalQueries);
        stats.put("pendingQueries", pendingQueries);
        stats.put("answeredQueries", answeredQueries);
        stats.put("totalKnowledgeDocuments", totalKnowledgeDocs);

        return stats;
    }
}

