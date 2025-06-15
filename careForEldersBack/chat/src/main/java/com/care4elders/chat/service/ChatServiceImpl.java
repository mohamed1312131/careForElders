package com.care4elders.chat.service;


import com.care4elders.chat.DTO.ChatDTO;
import com.care4elders.chat.DTO.MessageDTO;
import com.care4elders.chat.entity.Chat;
import com.care4elders.chat.entity.Message;
import com.care4elders.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

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
        // 1. Retrieve the chat
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        String patientId = chat.getPatientId();

        // 2. Create and save patient message
        List<Double> patientEmbedding = embeddingClient.getEmbedding(prompt);
        Message patientMessage = Message.builder()
                .sender("PATIENT")
                .content(prompt)
                .embedding(patientEmbedding)
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(patientMessage);

        // 3. Immediately send the user message via WebSocket
        ChatDTO intermediateChat = mapToDTO(chat);
       // messagingTemplate.convertAndSend("/topic/chat/" + chatId, intermediateChat);

        // 4. Save to memory and retrieve relevant context
        memoryService.savePatientMessage(patientId, prompt);
        List<String> memories = memoryService.retrieveRelevantMemories(patientId, prompt);

        // 5. Get patient info
        var patientInfo = patientGateway.getPatientInfo(patientId);

        // 6. Build the system prompt with context
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("You are a helpful health assistant for elderly patients. ")
                .append("Here's some information about the patient:\n")
                .append("- Name: ").append(patientInfo.getFirstName()).append("\n")
                .append("- Birth Date: ").append(patientInfo.getBirthDate()).append("\n\n");

        if (!memories.isEmpty()) {
            systemPrompt.append("Relevant previous conversations:\n");
            for (String memory : memories) {
                systemPrompt.append("- ").append(memory).append("\n");
            }
            systemPrompt.append("\n");
        }

        systemPrompt.append("Current conversation:\n")
                .append("Patient: ").append(prompt).append("\n")
                .append("Assistant: ");

        // 7. Generate AI response (stream this in a real implementation)
        String aiReply = ollamaClient.chat(systemPrompt.toString());

        // 8. Create and save AI message
        Message aiMessage = Message.builder()
                .sender("AI")
                .content(aiReply)
                .timestamp(Instant.now())
                .build();
        chat.getMessages().add(aiMessage);

        // 9. Store AI message in memory
        memoryService.saveAIMessage(patientId, aiReply);

        // 10. Save the updated chat
        Chat savedChat = chatRepository.save(chat);
        ChatDTO finalChat = mapToDTO(savedChat);

        // 11. Send the final updated chat via WebSocket
        //messagingTemplate.convertAndSend("/topic/chat/" + chatId, finalChat);

        return mapToDTO(savedChat);
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
