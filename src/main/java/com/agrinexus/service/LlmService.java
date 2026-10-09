package com.agrinexus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${llm.api.key:}")
    private String apiKey;

    @Value("${llm.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${llm.model:gpt-3.5-turbo}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateResponse(String systemPrompt, String userPrompt, String ragContext) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_LLM_API_KEY")) {
            try {
                return callExternalLlmApi(systemPrompt, userPrompt, ragContext);
            } catch (Exception e) {
                System.err.println("External LLM API call failed: " + e.getMessage() + ". Falling back to Agronomic RAG Knowledge Engine.");
            }
        }
        return generateDevelopmentFallbackResponse(userPrompt, ragContext);
    }

    private String callExternalLlmApi(String systemPrompt, String userPrompt, String ragContext) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String fullSystemMessage = systemPrompt + "\n\nRELEVANT AGRICULTURAL KNOWLEDGE CONTEXT:\n" + ragContext;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", fullSystemMessage),
                Map.of("role", "user", "content", userPrompt)
        ));
        requestBody.put("temperature", 0.4);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        return generateDevelopmentFallbackResponse(userPrompt, ragContext);
    }

    private String generateDevelopmentFallbackResponse(String userPrompt, String ragContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌱 **AgriNexus AI Assistant Response**\n\n");
        if (ragContext != null && !ragContext.trim().isEmpty()) {
            sb.append("Based on verified agricultural knowledge base guidelines:\n\n");
            sb.append(ragContext).append("\n\n");
            sb.append("💡 **Actionable Recommendation:** Ensure optimal soil moisture, apply recommended N-P-K nutrient ratios, and monitor crops closely during early vegetative and flowering stages.");
        } else {
            sb.append("For optimal crop health and yield, maintain regular irrigation, test soil pH regularly (6.0 - 7.5 range for most crops), and use balanced fertilization schedules. If you suspect pests or disease, upload a foliage photo in the Crop Image Analysis module for immediate neural network diagnosis!");
        }
        return sb.toString();
    }
}

