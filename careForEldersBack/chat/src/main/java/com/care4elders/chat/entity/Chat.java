package com.care4elders.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    private String patientId; // who owns this chat

    private Instant createdAt;

    @Builder.Default
    private List<Message> messages = new ArrayList<>();
}