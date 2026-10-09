package com.agrinexus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private KnowledgeDocument document;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String chunkText;

    private Integer chunkIndex;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String embeddingData;

    public KnowledgeChunk() {
    }

    public KnowledgeChunk(KnowledgeDocument document, String chunkText, Integer chunkIndex, String embeddingData) {
        this.document = document;
        this.chunkText = chunkText;
        this.chunkIndex = chunkIndex;
        this.embeddingData = embeddingData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KnowledgeDocument getDocument() {
        return document;
    }

    public void setDocument(KnowledgeDocument document) {
        this.document = document;
    }

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getEmbeddingData() {
        return embeddingData;
    }

    public void setEmbeddingData(String embeddingData) {
        this.embeddingData = embeddingData;
    }
}

