package com.care4elders.medicalrecord.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class AIService {
    private final OpenAiService openAiService;

    public AIService(@Value("${openai.api.key}") String openAiApiKey) {
        this.openAiService = new OpenAiService(openAiApiKey, Duration.ofSeconds(60));
    }
    public String summarizeMedicalNotes(String notes) {
        String prompt = "As a medical professional, create a concise summary of these clinical notes.\n" +
                "Focus on:\n" +
                "- Key diagnoses and symptoms\n" +
                "- Important test results\n" +
                "- Current treatments\n" +
                "- Action items\n\n" +
                "Notes:\n" + notes;

        // Using gpt-3.5-turbo-instruct (direct replacement for text-davinci-003)
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct") // Updated model
                .prompt(prompt)
                .temperature(0.3)
                .maxTokens(300)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();

        try {
            CompletionResult result = openAiService.createCompletion(completionRequest);
            return result.getChoices().get(0).getText().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

    // Alternative using chat completions API (recommended for newer applications)
    public String summarizeMedicalNotesWithChat(String notes) {
        String systemMessage = "You are a medical assistant summarizing patient notes for doctors. " +
                "Extract key clinical information and present it concisely.";

        ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo") // or "gpt-4" for better quality
                .messages(List.of(
                        new ChatMessage("system", systemMessage),
                        new ChatMessage("user", notes)
                ))
                .temperature(0.3)
                .maxTokens(300)
                .build();

        try {
            ChatCompletionResult result = openAiService.createChatCompletion(chatRequest);
            return result.getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }
}
