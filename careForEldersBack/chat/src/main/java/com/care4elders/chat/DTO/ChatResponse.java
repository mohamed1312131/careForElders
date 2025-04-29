package com.care4elders.chat.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ChatResponse {
    private String chatId;
    private List<MessageDTO> messages;
    private Instant createdAt;
    private Instant updatedAt;
}