package com.agrinexus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrinexus.entity.FarmerProfile;
import com.agrinexus.entity.User;

public interface FarmerProfileRepository
        extends JpaRepository<FarmerProfile, Long> {

    Optional<FarmerProfile> findByUser(User user);

    boolean existsByUser(User user);
}