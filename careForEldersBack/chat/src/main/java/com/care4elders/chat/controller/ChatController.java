package com.care4elders.chat.controller;

import com.care4elders.chat.DTO.AddMessageRequest;
import com.care4elders.chat.DTO.ChatResponse;
import com.care4elders.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{userId}")
    public ResponseEntity<ChatResponse> createChat(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.createChat(userId));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatResponse> addMessage(
            @PathVariable String chatId,
            @RequestBody AddMessageRequest request) {
        return ResponseEntity.ok(chatService.addMessage(chatId, request));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getChat(chatId));
    }
}
