package com.agrinexus.config;

import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.KnowledgeBaseService;
import com.agrinexus.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                            UserService userService,
                            KnowledgeBaseService knowledgeBaseService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed default Admin user
        if (!userRepository.existsByEmail("admin@agrinexus.com")) {
            userRepository.save(new User("AgriNexus Administrator", "admin@agrinexus.com",
                    passwordEncoder.encode("AdminPass123!"), Role.ADMIN));
            System.out.println("✅ Seeded default admin account: admin@agrinexus.com / AdminPass123!");
        } else {
            User admin = userRepository.findByEmail("admin@agrinexus.com").get();
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));
            userRepository.save(admin);
        }

        // Seed default Expert user
        if (!userRepository.existsByEmail("expert@agrinexus.com")) {
            userRepository.save(new User("Dr. Ramesh Sharma (Agronomist)", "expert@agrinexus.com",
                    passwordEncoder.encode("ExpertPass123!"), Role.EXPERT));
            System.out.println("✅ Seeded default expert account: expert@agrinexus.com / ExpertPass123!");
        } else {
            User expert = userRepository.findByEmail("expert@agrinexus.com").get();
            expert.setPassword(passwordEncoder.encode("ExpertPass123!"));
            userRepository.save(expert);
        }

        // Seed default Farmer user
        if (!userRepository.existsByEmail("farmer@agrinexus.com")) {
            userService.registerUser("Suresh Kumar", "farmer@agrinexus.com", "FarmerPass123!", "FARMER");
            System.out.println("✅ Seeded default farmer account: farmer@agrinexus.com / FarmerPass123!");
        }

        // Seed RAG Agricultural Knowledge Base
        if (knowledgeBaseService.getAllDocuments().isEmpty()) {
            knowledgeBaseService.addDocument(
                    "Rice Paddy Cultivation & Water Management",
                    "CROP_MANAGEMENT",
                    "Rice requires warm temperatures (20-38°C), high humidity (>70%), and standing water of 2-5cm during vegetative growth. Apply nitrogen in split doses at tillering and panicle initiation. Soil pH between 5.5 and 7.2 is optimal. Monitor regularly for stem borer and bacterial leaf blight.",
                    "ICAR Agricultural Extension Bulletin"
            );

            knowledgeBaseService.addDocument(
                    "Wheat Crop Management & Irrigation Guidelines",
                    "CROP_MANAGEMENT",
                    "Wheat is a Rabi crop requiring cool temperatures (10-25°C) and moderate rainfall (35-100mm). Critical irrigation stages include Crown Root Initiation (CRI at 20-25 days post-sowing), Tillering, Flowering, and Dough stage. Apply 120kg N, 60kg P2O5, and 40kg K2O per hectare.",
                    "National Agronomy Manual"
            );

            knowledgeBaseService.addDocument(
                    "Soil Health, pH & Fertilizer Management",
                    "SOIL_HEALTH",
                    "Soil pH measures acidity or alkalinity on a scale of 0-14. Soil pH of 6.0-7.5 provides maximum nutrient availability for most crops. Acidic soils (pH < 6.0) require agricultural lime (calcium carbonate) application. Alkaline soils (pH > 7.5) benefit from gypsum or organic mulch incorporation.",
                    "Soil Science Society Handbook"
            );

            knowledgeBaseService.addDocument(
                    "Tomato Leaf Disease Prevention & Treatment",
                    "DISEASE_MANAGEMENT",
                    "Common tomato leaf diseases include Early Blight (Alternaria solani), Late Blight (Phytophthora infestans), and Bacterial Spot. Prevent blight by pruning lower leaves touching soil, practicing crop rotation, and applying copper-based fungicides or mancozeb at early symptom onset.",
                    "Plant Pathology & Protection Guide"
            );

            System.out.println("📚 Seeded RAG Agricultural Knowledge Base and indexed vector chunks.");
        }
    }
}

