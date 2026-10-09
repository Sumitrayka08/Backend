package com.agrinexus.service;

import com.agrinexus.entity.KnowledgeDocument;
import com.agrinexus.repository.KnowledgeDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgeBaseService {

    private final KnowledgeDocumentRepository documentRepository;
    private final RagVectorSearchService vectorSearchService;

    public KnowledgeBaseService(KnowledgeDocumentRepository documentRepository,
                                 RagVectorSearchService vectorSearchService) {
        this.documentRepository = documentRepository;
        this.vectorSearchService = vectorSearchService;
    }

    @Transactional
    public KnowledgeDocument addDocument(String title, String category, String content, String source) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Document title is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Document content is required");
        }

        KnowledgeDocument doc = new KnowledgeDocument();
        doc.setTitle(title.trim());
        doc.setCategory(category != null ? category.trim() : "GENERAL");
        doc.setContent(content.trim());
        doc.setSource(source != null ? source.trim() : "ADMIN_UPLOAD");

        KnowledgeDocument saved = documentRepository.save(doc);

        // Immediately chunk and index for vector search
        vectorSearchService.chunkAndIndexDocument(saved);

        return saved;
    }

    public List<KnowledgeDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    public KnowledgeDocument getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
    }

    @Transactional
    public void deleteDocument(Long id) {
        KnowledgeDocument doc = getDocumentById(id);
        documentRepository.delete(doc);
    }
}

