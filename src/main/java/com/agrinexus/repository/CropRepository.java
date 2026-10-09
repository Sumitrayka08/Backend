package com.agrinexus.repository;

import com.agrinexus.entity.Crop;
import com.agrinexus.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarm(Farm farm);
    List<Crop> findByFarmId(Long farmId);
    List<Crop> findByFarmUserId(Long userId);
}