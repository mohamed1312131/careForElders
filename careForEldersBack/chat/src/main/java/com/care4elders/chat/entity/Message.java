package com.care4elders.chat.entity;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String role; // "user" or "ai"
    private String content;
    private Instant timestamp;
}