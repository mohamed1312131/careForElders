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
public class Patient {
    @Id
    private String patientId;
    private String medicalHistory;
    private List<String> assignedDoctorIds;
    private List<String> assignedCareAssistantIds;
}
