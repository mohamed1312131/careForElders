package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmbeddingClient {

    private final RestTemplate plainRestTemplate;

    public List<Double> getEmbedding(String text) {
        Map<String, String> request = Map.of("text", text);

        // Keep port 8000 (embedding-service port in docker-compose)
        Map<String, Object> response = plainRestTemplate.postForObject(
                "http://localhost:8000/embed",  // Correct port for embedding service
                request,
                Map.class
        );

        if (response == null || !response.containsKey("embedding")) {
            throw new RuntimeException("Embedding service failed or returned unexpected response.");
        }

        Object embeddingObj = response.get("embedding");
        if (!(embeddingObj instanceof List)) {
            throw new RuntimeException("Embedding service returned non-list embedding.");
        }

        try {
            return ((List<?>) embeddingObj).stream()
                    .map(o -> ((Number) o).doubleValue())
                    .toList();
        } catch (ClassCastException | NullPointerException e) {
            throw new RuntimeException("Error processing embedding list from service.", e);
        }
    }
}