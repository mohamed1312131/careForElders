package com.care4elders.planandexercise.DTO;

import com.care4elders.planandexercise.entity.PatientProgramAssignment;
import com.care4elders.planandexercise.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientProgramDTO {
    private String programId;
    private String name;
    private String description;
    private LocalDateTime assignedDate;
    private double completionPercentage;
    private String status;
    private int currentDay;
    private int totalDays;
    public PatientProgramDTO(Program program, PatientProgramAssignment assignment) {
        this.programId = program.getId();
        this.name = program.getName();
        this.description = program.getDescription();
        this.assignedDate = assignment.getAssignedDate();
        this.completionPercentage = assignment.getCompletionPercentage();
        this.status = assignment.getStatus();
        this.currentDay = assignment.getCurrentDay();
        this.totalDays = program.getDays().size();
    }
}