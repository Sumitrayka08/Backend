package com.agrinexus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrinexus.entity.Farm;
import com.agrinexus.entity.User;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByUser(User user);
}