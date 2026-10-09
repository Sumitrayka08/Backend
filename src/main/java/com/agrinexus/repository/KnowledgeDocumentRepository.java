package com.agrinexus.repository;

import com.agrinexus.entity.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {
    List<KnowledgeDocument> findByCategory(String category);
    List<KnowledgeDocument> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}

