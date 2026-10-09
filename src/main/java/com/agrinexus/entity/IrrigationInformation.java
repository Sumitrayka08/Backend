package com.agrinexus.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigation")
public class IrrigationInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    private String irrigationType;

    private String frequency;

    private String waterSource;

    private LocalDate lastIrrigationDate;

    private LocalDate nextIrrigationDate;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public IrrigationInformation() {
    }

    public IrrigationInformation(Farm farm, String irrigationType, String frequency,
                                  String waterSource, LocalDate lastIrrigationDate,
                                  LocalDate nextIrrigationDate, String notes) {
        this.farm = farm;
        this.irrigationType = irrigationType;
        this.frequency = frequency;
        this.waterSource = waterSource;
        this.lastIrrigationDate = lastIrrigationDate;
        this.nextIrrigationDate = nextIrrigationDate;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public String getIrrigationType() {
        return irrigationType;
    }

    public void setIrrigationType(String irrigationType) {
        this.irrigationType = irrigationType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;
    }

    public LocalDate getLastIrrigationDate() {
        return lastIrrigationDate;
    }

    public void setLastIrrigationDate(LocalDate lastIrrigationDate) {
        this.lastIrrigationDate = lastIrrigationDate;
    }

    public LocalDate getNextIrrigationDate() {
        return nextIrrigationDate;
    }

    public void setNextIrrigationDate(LocalDate nextIrrigationDate) {
        this.nextIrrigationDate = nextIrrigationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

