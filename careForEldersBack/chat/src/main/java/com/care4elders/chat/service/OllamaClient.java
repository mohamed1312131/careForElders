package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaClient {

    private final RestTemplate plainRestTemplate;

    public String generateAIResponse(String prompt) {
        Map<String, Object> response = plainRestTemplate.postForObject(
                "http://localhost:11434/api/generate", // Ollama local port
                Map.of(
                        "model", "gemma3:4b", // or whatever you loaded
                        "prompt", prompt
                ),
                Map.class
        );

        return (String) response.get("response");
    }
    public String chat(String prompt) {
        Map<String, Object> body = Map.of(
                "model", "gemma3:4b",  // Use the exact name from `ollama list`
                "prompt", prompt,
                "stream", false
        );

        Map<String, Object> response = plainRestTemplate.postForObject(
                "http://localhost:11434/api/generate",
                body,
                Map.class
        );

        if (response == null || !response.containsKey("response")) {
            throw new RuntimeException("Ollama returned null or invalid response");
        }

        return response.get("response").toString();
    }
}
