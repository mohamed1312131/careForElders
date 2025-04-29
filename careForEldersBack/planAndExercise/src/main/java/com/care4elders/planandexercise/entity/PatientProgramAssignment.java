package com.care4elders.planandexercise.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "patient_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientProgramAssignment {
    @Id
    private String id;
    private String patientId;     // Reference to User service's patient ID
    private String programId;     // Reference to Program
    private LocalDateTime assignedDate;
    private double completionPercentage;
    private Map<Integer, DayStatus> dayStatuses; // Key: dayNumber
    private LocalDateTime startDate;
    private LocalDateTime targetEndDate;
    private LocalDateTime actualEndDate;
    private String status; // ACTIVE, COMPLETED, PAUSED, ABANDONED
    private List<StatusHistory> statusHistory;
    private String doctorNotes;
    private String patientFeedback;
    private int currentDay; // Track current progress
    private LocalDateTime lastActivityDate;
    private boolean isDeleted; // Soft delete
}