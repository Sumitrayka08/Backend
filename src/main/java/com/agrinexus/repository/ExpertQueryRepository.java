package com.agrinexus.repository;

import com.agrinexus.entity.ExpertQuery;
import com.agrinexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertQueryRepository extends JpaRepository<ExpertQuery, Long> {
    List<ExpertQuery> findByFarmerOrderByCreatedAtDesc(User farmer);
    List<ExpertQuery> findByExpertOrderByCreatedAtDesc(User expert);
    List<ExpertQuery> findByStatusOrderByCreatedAtDesc(String status);
    List<ExpertQuery> findAllByOrderByCreatedAtDesc();
}