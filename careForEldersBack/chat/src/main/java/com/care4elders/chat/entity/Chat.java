package com.care4elders.chat.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {
    @Id
    private String chatId;
    private List<UserDTO> participants;
    private LocalDateTime createdAt;
    private boolean isActive;
}
