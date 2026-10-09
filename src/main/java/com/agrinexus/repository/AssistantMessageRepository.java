package com.agrinexus.repository;

import com.agrinexus.entity.AssistantMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistantMessageRepository extends JpaRepository<AssistantMessage, Long> {
    List<AssistantMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
