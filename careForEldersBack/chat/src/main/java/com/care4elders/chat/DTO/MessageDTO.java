package com.care4elders.chat.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageDTO {
    private String role;
    private String content;
    private Instant timestamp;
}