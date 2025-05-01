package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmbeddingClient {

    private final RestTemplate restTemplate;

    public List<Double> generateEmbedding(String text) {
        Map<String, Object> response = restTemplate.postForObject(
                "http://localhost:8000/embed",
                Map.of("text", text),
                Map.class
        );

        return (List<Double>) response.get("embedding");
    }
    public List<Double> getEmbedding(String text) {
        Map<String, String> requestBody = Map.of("text", text);

        Map<String, List<Double>> response = restTemplate.postForObject(
                "http://localhost:8000/embed",
                requestBody,
                Map.class
        );

        if (response == null || !response.containsKey("embedding")) {
            throw new RuntimeException("Failed to fetch embedding");
        }

        return response.get("embedding");
    }
}
