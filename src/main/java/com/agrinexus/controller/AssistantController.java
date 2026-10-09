package com.agrinexus.controller;

import com.agrinexus.entity.AssistantConversation;
import com.agrinexus.entity.AssistantMessage;
import com.agrinexus.entity.User;
import com.agrinexus.repository.UserRepository;
import com.agrinexus.service.RagAssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final RagAssistantService assistantService;
    private final UserRepository userRepository;

    public AssistantController(RagAssistantService assistantService, UserRepository userRepository) {
        this.assistantService = assistantService;
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> askQuestion(Authentication authentication, @RequestBody Map<String, Object> request) {
        User user = getCurrentUser(authentication);

        String question = (String) request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Question is required"));
        }

        Long conversationId = null;
        if (request.containsKey("conversationId") && request.get("conversationId") != null) {
            conversationId = Long.parseLong(request.get("conversationId").toString());
        }

        AssistantMessage botMessage = assistantService.processUserQuestion(user, conversationId, question);

        return ResponseEntity.ok(Map.of(
                "conversationId", botMessage.getConversation().getId(),
                "messageId", botMessage.getId(),
                "sender", botMessage.getSender(),
                "messageText", botMessage.getMessageText(),
                "sourcesUsed", botMessage.getSourcesUsed(),
                "createdAt", botMessage.getCreatedAt().toString()
        ));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<AssistantConversation>> getConversations(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(assistantService.getUserConversations(user));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<AssistantMessage>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(assistantService.getConversationMessages(id));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

