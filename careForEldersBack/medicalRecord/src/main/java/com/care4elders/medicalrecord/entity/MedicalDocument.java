package com.care4elders.medicalrecord.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_documents")
public class MedicalDocument {
    @Id
    private String id;
    private String userId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private byte[] data;
    private LocalDate uploadDate;
}
