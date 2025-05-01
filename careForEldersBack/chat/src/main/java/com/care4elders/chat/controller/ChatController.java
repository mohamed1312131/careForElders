package com.care4elders.chat.controller;


import com.care4elders.chat.DTO.ChatDTO;
import com.care4elders.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/new/{patientId}")
    public ChatDTO createNewChat(@PathVariable String patientId) {
        return chatService.createNewChat(patientId);
    }

    @PostMapping("/{chatId}/prompt")
    public ChatDTO addPatientPrompt(@PathVariable String chatId, @RequestBody String prompt) {
        return chatService.addPatientPrompt(chatId, prompt);
    }

    @GetMapping("/patient/{patientId}")
    public List<ChatDTO> getChatsForPatient(@PathVariable String patientId) {
        return chatService.getChatsForPatient(patientId);
    }

    @GetMapping("/{chatId}")
    public ChatDTO getChatById(@PathVariable String chatId) {
        return chatService.getChatById(chatId);
    }
}
