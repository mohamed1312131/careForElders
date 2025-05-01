package com.care4elders.chat.DTO;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String sender; // "PATIENT" or "AI"
    private String content;
    private Instant timestamp;
}