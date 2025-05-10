package com.care4elders.medicalrecord.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_notes")
public class MedicalNote {
    @Id
    private String id;
    private String userId;        // patient or provider
    private String authorId;      // doctor or nurse
    private String authorName;    // cached for display
    private String content;
    private LocalDateTime createdAt;
}
