package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.DTO.SummaryRequest;
import com.care4elders.medicalrecord.DTO.SummaryResponse;
import com.care4elders.medicalrecord.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping
    public ResponseEntity<?> summarizeNotes(@RequestBody SummaryRequest request) {

            String summary = summaryService.summarizeText(request.getText());
            return ResponseEntity.ok(new SummaryResponse(summary));
        }
    }




