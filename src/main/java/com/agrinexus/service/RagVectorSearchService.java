package com.agrinexus.service;

import com.agrinexus.entity.KnowledgeChunk;
import com.agrinexus.entity.KnowledgeDocument;
import com.agrinexus.repository.KnowledgeChunkRepository;
import com.agrinexus.repository.KnowledgeDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RagVectorSearchService {

    private final KnowledgeDocumentRepository documentRepository;
    private final KnowledgeChunkRepository chunkRepository;

    public RagVectorSearchService(KnowledgeDocumentRepository documentRepository,
                                  KnowledgeChunkRepository chunkRepository) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
    }

    @Transactional
    public void chunkAndIndexDocument(KnowledgeDocument document) {
        chunkRepository.deleteByDocumentId(document.getId());

        String content = document.getContent();
        if (content == null || content.trim().isEmpty()) return;

        List<String> rawChunks = splitIntoChunks(content, 400);
        int index = 0;
        for (String chunkText : rawChunks) {
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.setDocument(document);
            chunk.setChunkText(chunkText);
            chunk.setChunkIndex(index++);
            chunk.setEmbeddingData(generateSimpleEmbeddingString(chunkText));
            chunkRepository.save(chunk);
        }
    }

    public List<SearchResult> searchRelevantChunks(String query, int topK) {
        List<KnowledgeChunk> allChunks = chunkRepository.findAll();
        if (allChunks.isEmpty()) return Collections.emptyList();

        Set<String> queryTokens = tokenize(query);
        List<SearchResult> results = new ArrayList<>();

        for (KnowledgeChunk chunk : allChunks) {
            Set<String> chunkTokens = tokenize(chunk.getChunkText());
            double similarity = calculateJaccardSimilarity(queryTokens, chunkTokens);
            if (similarity > 0.05 || containsAnyKeyword(queryTokens, chunk.getChunkText())) {
                results.add(new SearchResult(chunk, similarity));
            }
        }

        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results.subList(0, Math.min(topK, results.size()));
    }

    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        return chunks;
    }

    private Set<String> tokenize(String text) {
        if (text == null) return Collections.emptySet();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+");
        return new HashSet<>(Arrays.asList(words));
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private boolean containsAnyKeyword(Set<String> keywords, String text) {
        String lowerText = text.toLowerCase();
        for (String kw : keywords) {
            if (kw.length() > 3 && lowerText.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private String generateSimpleEmbeddingString(String text) {
        return "dim:" + text.length() + "_hash:" + text.hashCode();
    }

    public static class SearchResult {
        public final KnowledgeChunk chunk;
        public final double score;

        public SearchResult(KnowledgeChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}

