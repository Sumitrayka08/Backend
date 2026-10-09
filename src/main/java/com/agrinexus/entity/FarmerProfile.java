package com.agrinexus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    private String address;

    private String state;

    private String district;

    private String village;

    private Double landArea;

    private String preferredLanguage;

    // Legacy compatibility fields
    private String location = "";
    private String landSize = "";
    private String soilType = "";
    private String preferredCrops = "";

    public FarmerProfile() {
    }

    @PrePersist
    @PreUpdate
    protected void ensureDefaults() {
        if (location == null) {
            location = "";
        }
        if (landSize == null) {
            landSize = "";
        }
        if (soilType == null) {
            soilType = "";
        }
        if (preferredCrops == null) {
            preferredCrops = "";
        }
    }

    public FarmerProfile(User user, String phone, String address, String state, String district, String village, Double landArea, String preferredLanguage) {
        this.user = user;
        this.phone = phone;
        this.address = address;
        this.state = state;
        this.district = district;
        this.village = village;
        this.landArea = landArea;
        this.preferredLanguage = preferredLanguage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Double getLandArea() {
        return landArea;
    }

    public void setLandArea(Double landArea) {
        this.landArea = landArea;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLandSize() {
        return landSize;
    }

    public void setLandSize(String landSize) {
        this.landSize = landSize;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getPreferredCrops() {
        return preferredCrops;
    }

    public void setPreferredCrops(String preferredCrops) {
        this.preferredCrops = preferredCrops;
    }
}