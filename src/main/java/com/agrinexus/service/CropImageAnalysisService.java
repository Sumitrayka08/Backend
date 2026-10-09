package com.agrinexus.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.agrinexus.dto.AiPredictionResponse;
import com.agrinexus.entity.CropImageAnalysis;
import com.agrinexus.entity.User;
import com.agrinexus.repository.CropImageAnalysisRepository;

@Service
public class CropImageAnalysisService {

    private final CropImageAnalysisRepository cropImageAnalysisRepository;

    private final RestClient restClient;

    public CropImageAnalysisService(
            CropImageAnalysisRepository cropImageAnalysisRepository,
            RestClient.Builder restClientBuilder) {

        this.cropImageAnalysisRepository =
                cropImageAnalysisRepository;

        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8000")
                .build();
    }

    public CropImageAnalysis analyzeImage(
            User user,
            String imageName,
            Path imagePath) {

        try {

            byte[] imageBytes =
                    Files.readAllBytes(imagePath);

            ByteArrayResource imageResource =
                    new ByteArrayResource(imageBytes) {

                        @Override
                        public String getFilename() {
                            return imageName;
                        }
                    };

            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add("file", imageResource);

            AiPredictionResponse prediction =
                    restClient
                            .post()
                            .uri("/predict")
                            .contentType(
                                    MediaType.MULTIPART_FORM_DATA)
                            .body(body)
                            .retrieve()
                            .body(AiPredictionResponse.class);

            if (prediction == null) {
                throw new RuntimeException(
                        "AI service returned an empty response");
            }

            String disease =
                    prediction.getDisease();

            Double confidence =
                    prediction.getConfidence();

            String recommendation = prediction.getRecommendation();
            if (recommendation == null || recommendation.trim().isEmpty()) {
                recommendation = generateRecommendation(disease);
            }

            CropImageAnalysis analysis =
                    new CropImageAnalysis();

            analysis.setUser(user);
            analysis.setImageName(imageName);
            analysis.setDisease(disease);
            analysis.setConfidence(confidence);
            analysis.setRecommendation(recommendation);
            analysis.setCreatedAt(LocalDateTime.now());

            return cropImageAnalysisRepository.save(analysis);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read uploaded image",
                    e);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to get prediction from AI service",
                    e);
        }
    }

    private String generateRecommendation(
            String disease) {

        if (disease == null) {

            return "Unable to determine crop disease.";
        }

        if (disease.contains("healthy")) {

            return "Crop appears healthy. Continue regular irrigation, "
                    + "fertilization and pest monitoring.";
        }

        if (disease.contains("Apple_scab")) {

            return "Apple scab detected. Remove infected leaves and fruit, "
                    + "improve air circulation and consider appropriate "
                    + "fungicide treatment according to agricultural guidance.";
        }

        if (disease.contains("Black_rot")) {

            return "Black rot detected. Remove infected plant material, "
                    + "maintain field sanitation and consider appropriate "
                    + "fungicide treatment.";
        }

        if (disease.contains("Early_blight")) {

            return "Early blight detected. Remove affected leaves, "
                    + "avoid overhead irrigation and maintain proper "
                    + "plant spacing.";
        }

        if (disease.contains("Late_blight")) {

            return "Late blight detected. Remove affected plant material, "
                    + "avoid prolonged leaf wetness and seek appropriate "
                    + "disease-management treatment.";
        }

        if (disease.contains("Bacterial_spot")) {

            return "Bacterial spot detected. Remove severely affected "
                    + "plant material, avoid overhead irrigation and "
                    + "maintain good field sanitation.";
        }

        if (disease.contains("Powdery_mildew")) {

            return "Powdery mildew detected. Improve air circulation, "
                    + "avoid excessive humidity and consider an appropriate "
                    + "fungicide treatment.";
        }

        return "Disease detected. Inspect the affected crop carefully "
                + "and consult an agricultural expert for appropriate "
                + "treatment.";
    }

    public List<CropImageAnalysis> getHistory(
            User user) {

        return cropImageAnalysisRepository
                .findByUser(user);
    }
}