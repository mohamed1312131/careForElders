package com.care4elders.blogforum.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.care4elders.blogforum.service.SpeechToTextService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/speech-to-text")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class SpeechToTextController {

    private final SpeechToTextService speechToTextService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptResponse> processAudio(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("language") String languageCode) {
        
        try {
            byte[] audioBytes = audioFile.getBytes();
            String transcript = speechToTextService.transcribeAudio(audioBytes, languageCode);
            return ResponseEntity.ok(new TranscriptResponse(transcript));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TranscriptResponse("Error processing audio"));
        }
    }

    public static class TranscriptResponse {
        private final String transcript;

        public TranscriptResponse(String transcript) {
            this.transcript = transcript;
        }

        public String getTranscript() {
            return transcript;
        }
    }
}