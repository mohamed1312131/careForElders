package com.care4elders.chat.service;


import com.care4elders.chat.DTO.AddMessageRequest;
import com.care4elders.chat.DTO.ChatResponse;
import com.care4elders.chat.DTO.MessageDTO;
import com.care4elders.chat.entity.Chat;
import com.care4elders.chat.entity.Message;
import com.care4elders.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final RestTemplate restTemplate;

    public ChatResponse createChat(String userId) {
        Chat chat = Chat.builder()
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat);
    }

    public ChatResponse addMessage(String chatId, AddMessageRequest request) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Fetch patient info from USER-SERVICE here if needed

        // Add the user's prompt
        chat.getMessages().add(Message.builder()
                .role("user")
                .content(request.getPrompt())
                .timestamp(Instant.now())
                .build());

        // Call Ollama to generate AI response
        String aiResponse = callOllamaAI(chat);

        // Add AI response
        chat.getMessages().add(Message.builder()
                .role("ai")
                .content(aiResponse)
                .timestamp(Instant.now())
                .build());

        chat.setUpdatedAt(Instant.now());
        chatRepository.save(chat);

        return toChatResponse(chat);
    }

    public ChatResponse getChat(String chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        return toChatResponse(chat);
    }

    private String callOllamaAI(Chat chat) {
        // Build prompt from history
        StringBuilder fullPrompt = new StringBuilder();
        for (Message message : chat.getMessages()) {
            fullPrompt.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        // TODO: Include Patient Info if needed
        // Example:
        // String patientInfo = getPatientInfo(chat.getUserId());

        // Send to Ollama API (I'll help you build this part next!)

        return "This is a mock AI response."; // ← Mock for now
    }

    private ChatResponse toChatResponse(Chat chat) {
        return ChatResponse.builder()
                .chatId(chat.getId())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .messages(chat.getMessages().stream()
                        .map(msg -> MessageDTO.builder()
                                .role(msg.getRole())
                                .content(msg.getContent())
                                .timestamp(msg.getTimestamp())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
