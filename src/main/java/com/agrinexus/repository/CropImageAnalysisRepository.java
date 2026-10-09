package com.agrinexus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrinexus.entity.CropImageAnalysis;
import com.agrinexus.entity.User;

public interface CropImageAnalysisRepository
        extends JpaRepository<CropImageAnalysis, Long> {

    List<CropImageAnalysis> findByUser(User user);
}