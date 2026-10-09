package com.agrinexus.dto;

import jakarta.validation.constraints.NotNull;

public class CropRecommendationRequest {

    private String soilType;

    @NotNull(message = "pH is required")
    private Double ph;

    @NotNull(message = "Nitrogen is required")
    private Double nitrogen;

    @NotNull(message = "Phosphorus is required")
    private Double phosphorus;

    @NotNull(message = "Potassium is required")
    private Double potassium;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    private Double humidity;

    @NotNull(message = "Rainfall is required")
    private Double rainfall;

    private String location;
    private String season;

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getNitrogen() { return nitrogen; }
    public void setNitrogen(Double nitrogen) { this.nitrogen = nitrogen; }

    public Double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(Double phosphorus) { this.phosphorus = phosphorus; }

    public Double getPotassium() { return potassium; }
    public void setPotassium(Double potassium) { this.potassium = potassium; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
}

