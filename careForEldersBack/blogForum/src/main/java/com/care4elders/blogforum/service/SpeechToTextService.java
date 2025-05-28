package com.care4elders.blogforum.service;

import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
@Service
public class SpeechToTextService {



    public String transcribeAudio(byte[] audioBytes, String languageCode) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Convert audio to LINEAR16 if needed (you might need audio conversion logic)
            ByteString audioData = ByteString.copyFrom(audioBytes);
            
            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setLanguageCode(languageCode)
                .setSampleRateHertz(16000) // Adjust based on your audio
                .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            
            return response.getResultsList().stream()
                .map(result -> result.getAlternativesList().isEmpty() ? "" : result.getAlternatives(0).getTranscript())
                .collect(Collectors.joining(" "));
        }
    }
}
