package com.care4elders.chat.service;

import java.util.List;

public interface MemoryService {
    void initializePatientMemory(String patientId);
    List<String> retrieveRelevantMemories(String patientId, String userInput);
}
