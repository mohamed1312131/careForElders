package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VectorDatabaseClient {

    private final RestTemplate restTemplate;

    private static final String CHROMA_DB_URL = "http://localhost:8001";

    public void createCollectionIfNotExists(String collectionName) {
        try {
            restTemplate.postForObject(
                    CHROMA_DB_URL + "/api/v1/collections",
                    Map.of("name", collectionName),
                    Void.class
            );
        } catch (Exception e) {
            // Collection already exists or another error
            System.out.println("Collection may already exist: " + e.getMessage());
        }
    }

    public void addEmbedding(String collectionName, String id, List<Double> embedding, String metadata) {
        // Ensure collection exists
        createCollectionIfNotExists(collectionName);

        restTemplate.postForObject(
                CHROMA_DB_URL + "/api/v1/collections/" + collectionName + "/add",
                Map.of(
                        "ids", List.of(id),
                        "embeddings", List.of(embedding),
                        "metadatas", List.of(Map.of("text", metadata))
                ),
                Void.class
        );
    }

    public List<String> querySimilarTexts(String collectionName, List<Double> embedding, int topK) {
        Map<String, Object> response = restTemplate.postForObject(
                CHROMA_DB_URL + "/api/v1/collections/" + collectionName + "/query",
                Map.of(
                        "query_embeddings", List.of(embedding),
                        "n_results", topK
                ),
                Map.class
        );

        List<Map<String, Object>> matches = (List<Map<String, Object>>) ((Map<String, Object>) response.get("documents")).get("0");
        return matches.stream()
                .map(match -> (String) match.get("text"))
                .toList();
    }
}