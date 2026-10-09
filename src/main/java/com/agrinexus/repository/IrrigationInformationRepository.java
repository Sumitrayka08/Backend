package com.agrinexus.repository;

import com.agrinexus.entity.IrrigationInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationInformationRepository extends JpaRepository<IrrigationInformation, Long> {
    List<IrrigationInformation> findByFarmId(Long farmId);
    Optional<IrrigationInformation> findTopByFarmIdOrderByUpdatedAtDesc(Long farmId);
}

