package com.agrinexus.controller;

import com.agrinexus.entity.KnowledgeDocument;
import com.agrinexus.service.KnowledgeBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/knowledge")
public class AdminKnowledgeController {

    private final KnowledgeBaseService knowledgeBaseService;

    public AdminKnowledgeController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping
    public ResponseEntity<?> addDocument(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String category = request.get("category");
        String content = request.get("content");
        String source = request.get("source");

        KnowledgeDocument doc = knowledgeBaseService.addDocument(title, category, content, source);
        return ResponseEntity.ok(doc);
    }

    @GetMapping
    public ResponseEntity<List<KnowledgeDocument>> getAllDocuments() {
        return ResponseEntity.ok(knowledgeBaseService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeDocument> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeBaseService.getDocumentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        knowledgeBaseService.deleteDocument(id);
        return ResponseEntity.ok(Map.of("message", "Knowledge document deleted and removed from RAG index", "id", id));
    }
}

