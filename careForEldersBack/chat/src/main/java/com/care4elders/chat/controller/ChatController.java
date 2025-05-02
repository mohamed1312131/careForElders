package com.care4elders.chat.controller;


import com.care4elders.chat.DTO.ChatDTO;
import com.care4elders.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;



import com.care4elders.chat.service.MemoryService;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemoryService memoryService;

    // 1. Create a new chat for a patient
    @PostMapping("/new/{patientId}")
    public ResponseEntity<ChatDTO> createNewChat(@PathVariable String patientId) {
        ChatDTO chat = chatService.createNewChat(patientId);
        memoryService.initializePatientMemory(patientId);
        return ResponseEntity.ok(chat);
    }

    // 2. Add a patient prompt to an existing chat
    @PostMapping("/{chatId}/prompt")
    public ResponseEntity<ChatDTO> addPrompt(@PathVariable String chatId, @RequestBody String prompt) {
        ChatDTO updatedChat = chatService.addPatientPrompt(chatId, prompt);
        return ResponseEntity.ok(updatedChat);
    }

    // 3. Get all chats for a patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ChatDTO>> getChatsForPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(chatService.getChatsForPatient(patientId));
    }

    // 4. Get a specific chat by ID
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    // 5. (Optional) Manual memory init for testing
    @PostMapping("/memory/init/{patientId}")
    public ResponseEntity<String> initMemory(@PathVariable String patientId) {
        memoryService.initializePatientMemory(patientId);
        return ResponseEntity.ok("Memory initialized for patient " + patientId);
    }
}
