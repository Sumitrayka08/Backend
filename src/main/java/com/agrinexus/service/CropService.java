package com.agrinexus.service;

import com.agrinexus.entity.Crop;
import com.agrinexus.entity.Farm;
import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.repository.CropRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final FarmService farmService;

    public CropService(CropRepository cropRepository, FarmService farmService) {
        this.cropRepository = cropRepository;
        this.farmService = farmService;
    }

    public Crop addCrop(User user, Long farmId, Crop crop) {
        Farm farm = farmService.getFarmByIdAndUser(farmId, user);
        crop.setFarm(farm);
        return cropRepository.save(crop);
    }

    public List<Crop> getCropsByUser(User user) {
        if (user.getRole() == Role.ADMIN) {
            return cropRepository.findAll();
        }
        return cropRepository.findByFarmUserId(user.getId());
    }

    public List<Crop> getCropsByFarm(Long farmId, User user) {
        Farm farm = farmService.getFarmByIdAndUser(farmId, user);
        return cropRepository.findByFarm(farm);
    }

    public Crop getCropByIdAndUser(Long id, User user) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found with id: " + id));

        if (user.getRole() != Role.ADMIN && !crop.getFarm().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: You do not own this crop record.");
        }
        return crop;
    }

    public Crop updateCrop(Long id, User user, Crop updatedCrop) {
        Crop crop = getCropByIdAndUser(id, user);

        if (updatedCrop.getCropName() != null) crop.setCropName(updatedCrop.getCropName());
        if (updatedCrop.getVariety() != null) crop.setVariety(updatedCrop.getVariety());
        if (updatedCrop.getSeason() != null) crop.setSeason(updatedCrop.getSeason());
        if (updatedCrop.getSowingDate() != null) crop.setSowingDate(updatedCrop.getSowingDate());
        if (updatedCrop.getExpectedHarvestDate() != null) crop.setExpectedHarvestDate(updatedCrop.getExpectedHarvestDate());
        if (updatedCrop.getStatus() != null) crop.setStatus(updatedCrop.getStatus());
        if (updatedCrop.getNotes() != null) crop.setNotes(updatedCrop.getNotes());

        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id, User user) {
        Crop crop = getCropByIdAndUser(id, user);
        cropRepository.delete(crop);
    }
}