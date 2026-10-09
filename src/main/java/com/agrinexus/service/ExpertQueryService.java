package com.agrinexus.service;

import com.agrinexus.entity.ExpertQuery;
import com.agrinexus.entity.User;
import com.agrinexus.repository.ExpertQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertQueryService {

    private final ExpertQueryRepository expertQueryRepository;

    public ExpertQueryService(ExpertQueryRepository expertQueryRepository) {
        this.expertQueryRepository = expertQueryRepository;
    }

    public ExpertQuery createQuery(User farmer, String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }
        ExpertQuery query = new ExpertQuery(farmer, question.trim());
        return expertQueryRepository.save(query);
    }

    public List<ExpertQuery> getFarmerQueries(User farmer) {
        return expertQueryRepository.findByFarmerOrderByCreatedAtDesc(farmer);
    }

    public List<ExpertQuery> getAvailableQueries() {
        return expertQueryRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    public List<ExpertQuery> getAllQueries() {
        return expertQueryRepository.findAllByOrderByCreatedAtDesc();
    }

    public ExpertQuery getQueryById(Long id) {
        return expertQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expert query not found with id: " + id));
    }

    public ExpertQuery respondToQuery(Long id, User expert, String responseText) {
        if (responseText == null || responseText.trim().isEmpty()) {
            throw new IllegalArgumentException("Response text is required");
        }
        ExpertQuery query = getQueryById(id);
        query.setExpert(expert);
        query.setResponse(responseText.trim());
        query.setStatus("ANSWERED");
        return expertQueryRepository.save(query);
    }

    public ExpertQuery updateStatus(Long id, String status) {
        ExpertQuery query = getQueryById(id);
        query.setStatus(status.toUpperCase());
        return expertQueryRepository.save(query);
    }
}