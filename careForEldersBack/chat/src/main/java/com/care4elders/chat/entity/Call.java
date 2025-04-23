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
public class Call {
    @Id
    private String callId;
    private String initiatorId;
    private List<String> participantIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CallType type;
    private CallStatus status;

    public enum CallType {
        AUDIO, VIDEO
    }

    public enum CallStatus {
        ONGOING, ENDED, MISSED
    }
}
