package com.agrinexus.service;

import com.agrinexus.entity.Farm;
import com.agrinexus.entity.SoilInformation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.SoilInformationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoilService {

    private final SoilInformationRepository soilRepository;
    private final FarmService farmService;

    public SoilService(SoilInformationRepository soilRepository, FarmService farmService) {
        this.soilRepository = soilRepository;
        this.farmService = farmService;
    }

    public SoilInformation saveSoilInfo(User user, Long farmId, SoilInformation soilInfo) {
        Farm farm = farmService.getFarmByIdAndUser(farmId, user);
        validateSoil(soilInfo);
        soilInfo.setFarm(farm);
        return soilRepository.save(soilInfo);
    }

    public List<SoilInformation> getSoilByFarm(Long farmId, User user) {
        farmService.getFarmByIdAndUser(farmId, user);
        return soilRepository.findByFarmId(farmId);
    }

    public SoilInformation updateSoilInfo(Long id, User user, SoilInformation updated) {
        SoilInformation existing = soilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Soil information record not found with id: " + id));

        farmService.getFarmByIdAndUser(existing.getFarm().getId(), user);
        validateSoil(updated);

        if (updated.getSoilType() != null) existing.setSoilType(updated.getSoilType());
        if (updated.getPh() != null) existing.setPh(updated.getPh());
        if (updated.getNitrogen() != null) existing.setNitrogen(updated.getNitrogen());
        if (updated.getPhosphorus() != null) existing.setPhosphorus(updated.getPhosphorus());
        if (updated.getPotassium() != null) existing.setPotassium(updated.getPotassium());
        if (updated.getOrganicMatter() != null) existing.setOrganicMatter(updated.getOrganicMatter());
        if (updated.getMoisture() != null) existing.setMoisture(updated.getMoisture());

        return soilRepository.save(existing);
    }

    private void validateSoil(SoilInformation soil) {
        if (soil.getPh() != null && (soil.getPh() < 0.0 || soil.getPh() > 14.0)) {
            throw new IllegalArgumentException("pH value must be between 0.0 and 14.0");
        }
        if (soil.getNitrogen() != null && soil.getNitrogen() < 0.0) {
            throw new IllegalArgumentException("Nitrogen value cannot be negative");
        }
        if (soil.getPhosphorus() != null && soil.getPhosphorus() < 0.0) {
            throw new IllegalArgumentException("Phosphorus value cannot be negative");
        }
        if (soil.getPotassium() != null && soil.getPotassium() < 0.0) {
            throw new IllegalArgumentException("Potassium value cannot be negative");
        }
        if (soil.getMoisture() != null && (soil.getMoisture() < 0.0 || soil.getMoisture() > 100.0)) {
            throw new IllegalArgumentException("Moisture percentage must be between 0 and 100");
        }
    }
}

