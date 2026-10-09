package com.agrinexus.dto;

import java.time.LocalDateTime;

import com.agrinexus.entity.CropImageAnalysis;

public class CropImageAnalysisResponse {

    private Long id;
    private String imageName;
    private String disease;
    private Double confidence;
    private String recommendation;
    private LocalDateTime createdAt;

    public CropImageAnalysisResponse(CropImageAnalysis analysis) {
        this.id = analysis.getId();
        this.imageName = analysis.getImageName();
        this.disease = analysis.getDisease();
        this.confidence = analysis.getConfidence();
        this.recommendation = analysis.getRecommendation();
        this.createdAt = analysis.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDisease() {
        return disease;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}