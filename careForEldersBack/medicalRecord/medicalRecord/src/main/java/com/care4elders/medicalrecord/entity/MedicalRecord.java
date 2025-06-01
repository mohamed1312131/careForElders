package com.care4elders.medicalrecord.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    private String id;
    private String userId; // Links to User entity
    private String bloodType;
    private List<String> allergies;
    private List<String> currentMedications;
    private List<String> chronicConditions;
    private String familyMedicalHistory;
    private String primaryCarePhysician;
    private String insuranceInformation;
    private LocalDate lastPhysicalExam;
    private List<Vaccination> vaccinations;
    private List<MedicalNote> notes;
    private List<MedicalDocument> documents;

    public MedicalRecord(String userId) {
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vaccination {
        private String name;
        private LocalDate dateAdministered;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalNote {
        private String content;
        private String authorId;
        private LocalDate createdDate;
    }
}
