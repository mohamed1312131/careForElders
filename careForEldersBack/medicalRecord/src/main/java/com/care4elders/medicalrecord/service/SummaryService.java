package com.care4elders.medicalrecord.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SummaryService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SummaryService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    public String summarizeText(String text) {
        try {
            String apiUrl = "https://api-inference.huggingface.co/models/" + model;

            // Medical-focused prompt
            String medicalPrompt = "Create a concise clinical summary for a doctor including:\n" +
                    "- Key diagnoses\n" +
                    "- Critical vitals/labs\n" +
                    "- Current medications\n" +
                    "- Urgent concerns\n" +
                    "- Recommended actions\n\n" +
                    "Clinical notes:\n" + text;

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Medical-optimized parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_length", 200);
            parameters.put("min_length", 50);
            parameters.put("temperature", 0.3);
            parameters.put("repetition_penalty", 1.5);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", medicalPrompt);
            requestBody.put("parameters", parameters);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl,
                    request,
                    String.class
            );

            // Parse response
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.isArray() && root.size() > 0) {
                return root.get(0).get("summary_text").asText();
            }
            throw new RuntimeException("Unexpected response format");

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

}
