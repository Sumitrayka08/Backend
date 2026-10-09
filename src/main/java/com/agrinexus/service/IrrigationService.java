package com.agrinexus.service;

import com.agrinexus.entity.Farm;
import com.agrinexus.entity.IrrigationInformation;
import com.agrinexus.entity.User;
import com.agrinexus.repository.IrrigationInformationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IrrigationService {

    private final IrrigationInformationRepository irrigationRepository;
    private final FarmService farmService;

    public IrrigationService(IrrigationInformationRepository irrigationRepository, FarmService farmService) {
        this.irrigationRepository = irrigationRepository;
        this.farmService = farmService;
    }

    public IrrigationInformation saveIrrigation(User user, Long farmId, IrrigationInformation info) {
        Farm farm = farmService.getFarmByIdAndUser(farmId, user);
        info.setFarm(farm);
        return irrigationRepository.save(info);
    }

    public List<IrrigationInformation> getIrrigationByFarm(Long farmId, User user) {
        farmService.getFarmByIdAndUser(farmId, user);
        return irrigationRepository.findByFarmId(farmId);
    }

    public IrrigationInformation updateIrrigation(Long id, User user, IrrigationInformation updated) {
        IrrigationInformation existing = irrigationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Irrigation record not found with id: " + id));

        farmService.getFarmByIdAndUser(existing.getFarm().getId(), user);

        if (updated.getIrrigationType() != null) existing.setIrrigationType(updated.getIrrigationType());
        if (updated.getFrequency() != null) existing.setFrequency(updated.getFrequency());
        if (updated.getWaterSource() != null) existing.setWaterSource(updated.getWaterSource());
        if (updated.getLastIrrigationDate() != null) existing.setLastIrrigationDate(updated.getLastIrrigationDate());
        if (updated.getNextIrrigationDate() != null) existing.setNextIrrigationDate(updated.getNextIrrigationDate());
        if (updated.getNotes() != null) existing.setNotes(updated.getNotes());

        return irrigationRepository.save(existing);
    }
}

