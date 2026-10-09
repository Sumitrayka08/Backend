package com.agrinexus.repository;

import com.agrinexus.entity.SoilInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoilInformationRepository extends JpaRepository<SoilInformation, Long> {
    List<SoilInformation> findByFarmId(Long farmId);
    Optional<SoilInformation> findTopByFarmIdOrderByUpdatedAtDesc(Long farmId);
}

