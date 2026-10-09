package com.agrinexus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farms")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmName;

    private String location;

    private Double area;

    private String soilType;

    private String irrigationMethod;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Farm() {
    }

    public Farm(String farmName, String location, Double area,
                String soilType, String irrigationMethod, User user) {
        this.farmName = farmName;
        this.location = location;
        this.area = area;
        this.soilType = soilType;
        this.irrigationMethod = irrigationMethod;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getIrrigationMethod() {
        return irrigationMethod;
    }

    public void setIrrigationMethod(String irrigationMethod) {
        this.irrigationMethod = irrigationMethod;
    }

    public String getIrrigationType() {
        return irrigationMethod;
    }

    public void setIrrigationType(String irrigationType) {
        this.irrigationMethod = irrigationType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}