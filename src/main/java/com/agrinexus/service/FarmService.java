package com.agrinexus.service;

import com.agrinexus.entity.Farm;
import com.agrinexus.entity.Role;
import com.agrinexus.entity.User;
import com.agrinexus.repository.FarmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmService {

    private final FarmRepository farmRepository;

    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    public Farm addFarm(User user, Farm farm) {
        farm.setUser(user);
        return farmRepository.save(farm);
    }

    public List<Farm> getFarmsByUser(User user) {
        if (user.getRole() == Role.ADMIN) {
            return farmRepository.findAll();
        }
        return farmRepository.findByUser(user);
    }

    public Farm getFarmByIdAndUser(Long id, User user) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found with id: " + id));

        if (user.getRole() != Role.ADMIN && !farm.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: You do not own this farm.");
        }
        return farm;
    }

    public Farm updateFarm(Long id, User user, Farm updatedFarm) {
        Farm farm = getFarmByIdAndUser(id, user);

        if (updatedFarm.getFarmName() != null) farm.setFarmName(updatedFarm.getFarmName());
        if (updatedFarm.getLocation() != null) farm.setLocation(updatedFarm.getLocation());
        if (updatedFarm.getArea() != null) farm.setArea(updatedFarm.getArea());
        if (updatedFarm.getSoilType() != null) farm.setSoilType(updatedFarm.getSoilType());
        if (updatedFarm.getIrrigationMethod() != null) farm.setIrrigationMethod(updatedFarm.getIrrigationMethod());

        return farmRepository.save(farm);
    }

    public void deleteFarm(Long id, User user) {
        Farm farm = getFarmByIdAndUser(id, user);
        farmRepository.delete(farm);
    }
}