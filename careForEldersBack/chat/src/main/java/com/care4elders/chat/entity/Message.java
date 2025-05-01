package com.care4elders.chat.entity;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String sender; // "PATIENT" or "AI"
    private String content;
    private List<Double> embedding; // embedding vector for retrieval
    private Instant timestamp;
}