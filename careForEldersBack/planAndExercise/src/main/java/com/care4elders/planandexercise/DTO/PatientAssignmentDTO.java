package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAssignmentDTO {
    private String patientId;
    private LocalDateTime assignedDate;
    private double completionPercentage;
    private String status;
    private String name;
    private String email;
}
