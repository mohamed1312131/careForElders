package com.care4elders.chat.service;

import java.util.List;

public interface MemoryService {
    void initializePatientMemory(String patientId);
    List<String> retrieveRelevantMemories(String patientId, String userInput);
    void savePatientMessage(String patientId, String message);
    void saveAIMessage(String patientId, String message);
}
