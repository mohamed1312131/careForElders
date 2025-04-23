package com.care4elders.chat.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareAssistant {
    @Id
    private String assistantId;
    private String qualification;
    private boolean isAvailable;
    private List<String> assignedPatientIds;
}
