package com.care4elders.medicalrecord.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    private String id;
    private String patientId;
    private String doctorId;
    private String nurseId;
    private LocalDateTime appointmentDate;
    private String treatment;
    private String status; // upcoming, completed, canceled

}
