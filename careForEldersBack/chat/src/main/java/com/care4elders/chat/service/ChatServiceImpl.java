package com.care4elders.chat.service;


import com.care4elders.chat.DTO.ChatDTO;
import com.care4elders.chat.DTO.MessageDTO;
import com.care4elders.chat.entity.Chat;
import com.care4elders.chat.entity.Message;
import com.care4elders.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final EmbeddingClient embeddingClient;

    @Override
    public ChatDTO createNewChat(String patientId) {
        Chat chat = Chat.builder()
                .patientId(patientId)
                .createdAt(Instant.now())
                .build();
        return mapToDTO(chatRepository.save(chat));
    }

    @Override
    public ChatDTO addPatientPrompt(String chatId, String prompt) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // 1. Add Patient Message
        Message patientMessage = Message.builder()
                .sender("PATIENT")
                .content(prompt)
                .embedding(embeddingClient.generateEmbedding(prompt))
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(patientMessage);

        // 2. TODO: retrieve relevant memories (later)
        // 3. TODO: ask Ollama for AI response (later)
        // 4. Add AI Message (fake for now)
        Message aiMessage = Message.builder()
                .sender("AI")
                .content("This is a placeholder AI response.")
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(aiMessage);

        return mapToDTO(chatRepository.save(chat));
    }

    @Override
    public List<ChatDTO> getChatsForPatient(String patientId) {
        return chatRepository.findAllByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatDTO getChatById(String chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        return mapToDTO(chat);
    }

    private ChatDTO mapToDTO(Chat chat) {
        return ChatDTO.builder()
                .id(chat.getId())
                .patientId(chat.getPatientId())
                .createdAt(chat.getCreatedAt())
                .messages(
                        chat.getMessages().stream()
                                .map(m -> MessageDTO.builder()
                                        .sender(m.getSender())
                                        .content(m.getContent())
                                        .timestamp(m.getTimestamp())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
