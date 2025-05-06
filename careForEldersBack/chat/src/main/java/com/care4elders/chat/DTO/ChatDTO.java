package com.care4elders.chat.DTO;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private String id;
    private String patientId;
    private Instant createdAt;
    private List<MessageDTO> messages;
}
