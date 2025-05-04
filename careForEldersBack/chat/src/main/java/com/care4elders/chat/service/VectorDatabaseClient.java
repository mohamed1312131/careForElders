package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VectorDatabaseClient {

    private final RestTemplate plainRestTemplate;
    private static final String CHROMA_DB_URL = "http://localhost:8101";

    // Get or create collection and return its UUID
    public String getOrCreateCollection(String collectionName) {
        try {
            // Check if collection exists
            return getCollectionUUID(collectionName);
        } catch (RuntimeException e) {
            // Create the collection and return its UUID
            return createCollection(collectionName);
        }
    }

    private String getCollectionUUID(String collectionName) {
        List<Map<String, Object>> collections = plainRestTemplate.getForObject(
                CHROMA_DB_URL + "/api/v1/collections",
                List.class
        );

        if (collections == null) {
            throw new RuntimeException("Failed to list collections");
        }

        return collections.stream()
                .filter(c -> collectionName.equals(c.get("name")))
                .findFirst()
                .map(c -> (String) c.get("id"))
                .orElseThrow(() -> new RuntimeException("Collection not found: " + collectionName));
    }

    private String createCollection(String collectionName) {
        Map<String, Object> response = plainRestTemplate.postForObject(
                CHROMA_DB_URL + "/api/v1/collections",
                Map.of("name", collectionName),
                Map.class
        );

        if (response == null || !response.containsKey("id")) {
            throw new RuntimeException("Failed to create collection: " + collectionName);
        }
        return (String) response.get("id");
    }

    public void addEmbedding(String collectionName, String id, List<Double> embedding, String text) {
        // Get or create the collection
        String collectionId = getOrCreateCollection(collectionName);

        // Convert to floats
        List<Float> floatEmbedding = embedding.stream()
                .map(Double::floatValue)
                .toList();

        var body = Map.of(
                "ids", List.of(id),
                "embeddings", List.of(floatEmbedding),
                "documents", List.of(text),
                "metadatas", List.of(Map.of("source", "chat-service"))
        );

        plainRestTemplate.postForObject(
                CHROMA_DB_URL + "/api/v1/collections/" + collectionId + "/add",
                body,
                Void.class
        );
    }

    public List<String> querySimilarTexts(String collectionName, List<Double> embedding, int topK) {
        String collectionId = getCollectionUUID(collectionName);
        List<Float> floatEmbedding = embedding.stream()
                .map(Double::floatValue)
                .toList();

        Map<String, Object> response = plainRestTemplate.postForObject(
                CHROMA_DB_URL + "/api/v1/collections/" + collectionId + "/query",
                Map.of(
                        "query_embeddings", List.of(floatEmbedding),
                        "n_results", topK
                ),
                Map.class
        );

        List<List<String>> docs = (List<List<String>>) response.get("documents");
        return docs != null && !docs.isEmpty() ? docs.get(0) : List.of();
    }
}