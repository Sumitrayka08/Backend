package com.agrinexus.service;

import com.agrinexus.entity.FarmerProfile;
import com.agrinexus.entity.User;
import com.agrinexus.repository.FarmerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerProfileService {

    private final FarmerProfileRepository farmerProfileRepository;

    public FarmerProfileService(FarmerProfileRepository farmerProfileRepository) {
        this.farmerProfileRepository = farmerProfileRepository;
    }

    public FarmerProfile getProfile(User user) {
        return farmerProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    FarmerProfile newProfile = new FarmerProfile();
                    newProfile.setUser(user);
                    return farmerProfileRepository.save(newProfile);
                });
    }

    @Transactional
    public FarmerProfile updateProfile(User user, String phone, String address, String state,
                                        String district, String village, Double landArea,
                                        String preferredLanguage, String location,
                                        String landSize, String soilType, String preferredCrops) {
        FarmerProfile profile = getProfile(user);

        if (phone != null) profile.setPhone(phone);
        if (address != null) profile.setAddress(address);
        if (state != null) profile.setState(state);
        if (district != null) profile.setDistrict(district);
        if (village != null) profile.setVillage(village);
        if (landArea != null) profile.setLandArea(landArea);
        if (preferredLanguage != null) profile.setPreferredLanguage(preferredLanguage);
        if (location != null) profile.setLocation(location);
        if (landSize != null) profile.setLandSize(landSize);
        if (soilType != null) profile.setSoilType(soilType);
        if (preferredCrops != null) profile.setPreferredCrops(preferredCrops);

        return farmerProfileRepository.save(profile);
    }
}