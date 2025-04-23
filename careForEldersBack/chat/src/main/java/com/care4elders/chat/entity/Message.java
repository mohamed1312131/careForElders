package com.care4elders.chat.entity;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private String messageId;
    private String chatId;
    private String senderId; // userId from user-service
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    private MessageType type;

    public enum MessageType {
        TEXT, IMAGE, VIDEO, FILE
    }
}
