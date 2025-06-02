package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.service.AIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeNotes(@RequestBody Map<String, String> request) {
        try {
            String notes = request.get("text");
            String summary = aiService.summarizeMedicalNotes(notes);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating summary: " + e.getMessage());
        }
    }
}
