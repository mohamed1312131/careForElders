package com.care4elders.chat.controller;

import com.care4elders.chat.DTO.ChatDTO;

import com.care4elders.chat.DTO.MessageDTO;
import com.care4elders.chat.DTO.WebSocketMessageDTO;
import com.care4elders.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketMessageDTO message) {
        // Process message
        ChatDTO updatedChat = chatService.addPatientPrompt(message.getChatId(), message.getContent());

        // Extract the last message (AI response)
        List<MessageDTO> messages = updatedChat.getMessages();
        if (!messages.isEmpty()) {
            MessageDTO latestMessage = messages.get(messages.size() - 1);

            // Send only the AI response
            if ("AI".equals(latestMessage.getSender())) {
                messagingTemplate.convertAndSend(
                        "/topic/chat/" + message.getChatId(),
                        latestMessage
                );
            }
        }
    }


    @MessageMapping("/chat.newChat")
    public void newChat(@Payload String patientId) {
        ChatDTO newChat = chatService.createNewChat(patientId);
        messagingTemplate.convertAndSend("/topic/user/" + patientId + "/newChat", newChat);
    }
}
