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
    private final MemoryService memoryService;
    private final OllamaClient ollamaClient;
    private final PatientGateway patientGateway;

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

        String patientId = chat.getPatientId();

        // 1. Save patient message
        List<Double> patientEmbedding = embeddingClient.getEmbedding(prompt);
        Message patientMessage = Message.builder()
                .sender("PATIENT")
                .content(prompt)
                .embedding(patientEmbedding)
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(patientMessage);

        // 2. Memory: enrich and retrieve relevant past info
        memoryService.savePatientMessage(patientId, prompt); // enrich
        List<String> memories = memoryService.retrieveRelevantMemories(patientId, prompt);

        // 3. Get patient info from USER-SERVICE
        var patientInfo = patientGateway.getPatientInfo(patientId);

        // 4. Build prompt for AI
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("Patient Info:\n")
                .append("- Name: ").append(patientInfo.getFirstName()).append("\n")
                .append("- Birth Date: ").append(patientInfo.getBirthDate()).append("\n\n");

        if (!memories.isEmpty()) {
            systemPrompt.append("Relevant Information:\n");
            for (String memory : memories) {
                systemPrompt.append("- ").append(memory).append("\n");
            }
            systemPrompt.append("\n");
        }

        systemPrompt.append("Current User Prompt:\n").append(prompt);

        // 5. Generate AI Response
        String aiReply = ollamaClient.chat(systemPrompt.toString());

        // 6. Save AI message
        Message aiMessage = Message.builder()
                .sender("AI")
                .content(aiReply)
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(aiMessage);

        // 7. Store AI message in memory too
        memoryService.saveAIMessage(patientId, aiReply);

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
