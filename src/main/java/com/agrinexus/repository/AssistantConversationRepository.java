package com.agrinexus.repository;

import com.agrinexus.entity.AssistantConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistantConversationRepository extends JpaRepository<AssistantConversation, Long> {
    List<AssistantConversation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
