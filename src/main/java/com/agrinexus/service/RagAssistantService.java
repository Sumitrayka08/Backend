package com.agrinexus.service;

import com.agrinexus.entity.*;
import com.agrinexus.repository.AssistantConversationRepository;
import com.agrinexus.repository.AssistantMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagAssistantService {

    private final RagVectorSearchService vectorSearchService;
    private final LlmService llmService;
    private final AssistantConversationRepository conversationRepository;
    private final AssistantMessageRepository messageRepository;

    public RagAssistantService(RagVectorSearchService vectorSearchService,
                                LlmService llmService,
                                AssistantConversationRepository conversationRepository,
                                AssistantMessageRepository messageRepository) {
        this.vectorSearchService = vectorSearchService;
        this.llmService = llmService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public AssistantMessage processUserQuestion(User user, Long conversationId, String question) {
        AssistantConversation conversation;
        if (conversationId != null) {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
        } else {
            String title = question.length() > 40 ? question.substring(0, 37) + "..." : question;
            conversation = new AssistantConversation(user, title);
            conversation = conversationRepository.save(conversation);
        }

        // Save user message
        AssistantMessage userMsg = new AssistantMessage(conversation, "USER", question, null);
        messageRepository.save(userMsg);

        // Vector search for top RAG chunks
        List<RagVectorSearchService.SearchResult> searchResults = vectorSearchService.searchRelevantChunks(question, 3);
        String ragContext = searchResults.stream()
                .map(r -> "- [" + r.chunk.getDocument().getTitle() + "]: " + r.chunk.getChunkText())
                .collect(Collectors.joining("\n\n"));

        String sources = searchResults.stream()
                .map(r -> r.chunk.getDocument().getTitle())
                .distinct()
                .collect(Collectors.joining(", "));

        if (sources.isEmpty()) {
            sources = "AgriNexus Agricultural Advisory Knowledge Engine";
        }

        String systemPrompt = "You are AgriNexus AI, an expert agricultural domain assistant assisting farmers with crop management, soil health, disease prevention, and farming practices.";
        String answer = llmService.generateResponse(systemPrompt, question, ragContext);

        // Save bot response
        AssistantMessage botMsg = new AssistantMessage(conversation, "ASSISTANT", answer, sources);
        return messageRepository.save(botMsg);
    }

    public List<AssistantConversation> getUserConversations(User user) {
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<AssistantMessage> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}

