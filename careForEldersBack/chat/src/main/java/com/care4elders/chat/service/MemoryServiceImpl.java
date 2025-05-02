package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private final PatientGateway patientGateway;
    private final EmbeddingClient embeddingClient;
    private final VectorDatabaseClient vectorDatabaseClient;

    @Override
    public void initializePatientMemory(String patientId) {
        String collectionName = "patient-" + patientId;

        // Fetch patient info
        var patientInfo = patientGateway.getPatientInfo(patientId);

        // Build memory data
        String summary = "Patient Info: Name=" + patientInfo.getFirstName() + ", BirthDate=" + patientInfo.getBirthDate();
        vectorDatabaseClient.getOrCreateCollection(collectionName);
        // Generate embedding
        List<Double> embedding = embeddingClient.getEmbedding(summary);

        // Store in vector DB
        vectorDatabaseClient.addEmbedding(
                collectionName,
                "patient-info", // ID
                embedding,
                summary
        );
    }

    @Override
    public List<String> retrieveRelevantMemories(String patientId, String userInput) {
        String collectionName = "patient-" + patientId;
        List<Double> inputEmbedding = embeddingClient.getEmbedding(userInput);

        return vectorDatabaseClient.querySimilarTexts(collectionName, inputEmbedding, 5);
    }

    @Override
    public void savePatientMessage(String patientId, String message) {
        saveToMemory("patient", patientId, message);
    }

    @Override
    public void saveAIMessage(String patientId, String message) {
        saveToMemory("ai", patientId, message);
    }

    private void saveToMemory(String sender, String patientId, String text) {
        String collectionName = "patient-" + patientId;
        String id = sender + "-" + UUID.randomUUID();

        List<Double> embedding = embeddingClient.getEmbedding(text);

        vectorDatabaseClient.addEmbedding(
                collectionName,
                id,
                embedding,
                text
        );
    }
}
