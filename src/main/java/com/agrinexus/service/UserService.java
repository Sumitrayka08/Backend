package com.agrinexus.service;

import com.agrinexus.entity.FarmerProfile;
import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.repository.FarmerProfileRepository;
import com.agrinexus.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            FarmerProfileRepository farmerProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User registerUser(String name, String email, String password, String roleStr) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String cleanEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email already registered: " + cleanEmail);
        }

        Role userRole = Role.FARMER;
        if (roleStr != null && !roleStr.trim().isEmpty()) {
            String requestedRoleStr = roleStr.trim().toUpperCase();
            if ("ADMIN".equals(requestedRoleStr) || "ROLE_ADMIN".equals(requestedRoleStr)) {
                throw new IllegalArgumentException("Public registration of ADMIN role is not allowed");
            }
            try {
                userRole = Role.valueOf(requestedRoleStr.replace("ROLE_", ""));
            } catch (IllegalArgumentException e) {
                userRole = Role.FARMER;
            }
        }

        User user = new User();
        user.setName(name);
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        // Automatically create farmer profile if role is FARMER
        if (userRole == Role.FARMER) {
            FarmerProfile profile = new FarmerProfile();
            profile.setUser(savedUser);
            profile.setLocation("");
            farmerProfileRepository.save(profile);
        }

        return savedUser;
    }

    public String loginUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        String cleanEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtService.generateToken(user.getEmail(), user.getRole().name());
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}