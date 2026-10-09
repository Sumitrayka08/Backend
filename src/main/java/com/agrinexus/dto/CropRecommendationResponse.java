package com.agrinexus.dto;

public class CropRecommendationResponse {

    private Long id;
    private String recommendedCrop;
    private Double confidenceScore;
    private String reason;
    private String guidance;
    private String methodUsed; // RULE_ENGINE or ML_MODEL

    public CropRecommendationResponse() {
    }

    public CropRecommendationResponse(Long id, String recommendedCrop, Double confidenceScore,
                                      String reason, String guidance, String methodUsed) {
        this.id = id;
        this.recommendedCrop = recommendedCrop;
        this.confidenceScore = confidenceScore;
        this.reason = reason;
        this.guidance = guidance;
        this.methodUsed = methodUsed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecommendedCrop() { return recommendedCrop; }
    public void setRecommendedCrop(String recommendedCrop) { this.recommendedCrop = recommendedCrop; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getGuidance() { return guidance; }
    public void setGuidance(String guidance) { this.guidance = guidance; }

    public String getMethodUsed() { return methodUsed; }
    public void setMethodUsed(String methodUsed) { this.methodUsed = methodUsed; }
}

