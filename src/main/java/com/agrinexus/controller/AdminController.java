package com.agrinexus.controller;

import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String role) {
        if (role != null && !role.trim().isEmpty()) {
            Role userRole = Role.valueOf(role.trim().toUpperCase());
            return ResponseEntity.ok(adminService.getUsersByRole(userRole));
        }
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String roleStr = request.get("role");
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Role string is required");
        }
        Role newRole = Role.valueOf(roleStr.trim().toUpperCase());
        User updatedUser = adminService.updateUserRole(id, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }
}

