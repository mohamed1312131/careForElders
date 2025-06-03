package com.care4elders.planandexercise.DTO;

import com.care4elders.planandexercise.entity.DayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAssignmentDetailsDTO {
    private String assignmentId;
    private String programId;
    private String programName;
    private String patientId;
    private String patientName;
    private LocalDateTime assignedDate;
    private String status;
    private int currentDay;
    private double completionPercentage;
    private Map<Integer, DayStatus> dayStatuses;
    private LocalDateTime lastActivityDate;
    private LocalDateTime actualEndDate;
}