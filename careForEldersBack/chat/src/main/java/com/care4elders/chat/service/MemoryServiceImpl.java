package com.care4elders.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private final PatientGateway patientGateway;
    private final EmbeddingClient embeddingClient;
    private final VectorDatabaseClient vectorDatabaseClient;

    @Override
    public void initializePatientMemory(String patientId) {
        String collectionName = "patient-" + patientId;

        // Fetch patient-related data
        var patientInfo = patientGateway.getPatientInfo(patientId);
        var medicalRecord = patientGateway.getMedicalRecord(patientId);
        var plan = patientGateway.getPlan(patientId);
        var exercise = patientGateway.getExercise(patientId);

        List<MemoryItem> memories = new ArrayList<>();
        memories.add(new MemoryItem("patient-info", "Patient Info: Name=" + patientInfo.getName() + ", Age=" + patientInfo.getAge()));
        memories.add(new MemoryItem("medical-record", "Medical Record: Diagnosis=" + medicalRecord.getDiagnosis()));
        memories.add(new MemoryItem("plan", "Plan: " + plan.getDescription()));
        memories.add(new MemoryItem("exercise", "Exercise Routine: " + exercise.getRoutine()));

        // Embed and store
        for (MemoryItem memory : memories) {
            List<Double> embedding = embeddingClient.getEmbedding(memory.content());
            vectorDatabaseClient.addEmbedding(
                    collectionName,
                    memory.id(),
                    embedding,
                    memory.content()
            );
        }
    }

    @Override
    public List<String> retrieveRelevantMemories(String patientId, String userInput) {
        String collectionName = "patient-" + patientId;
        List<Double> inputEmbedding = embeddingClient.getEmbedding(userInput);

        return vectorDatabaseClient.querySimilarTexts(collectionName, inputEmbedding, 5);
    }

    private record MemoryItem(String id, String content) {}
}
