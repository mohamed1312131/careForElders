package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProgramDetailsWithProgressDTO {
    private String programId;
    private String programName;
    private String description;
    private double completionPercentage;
    private int currentDay;
    private List<DayDetailsDTO> days;
    private String assignmentId; // Add this field

    // Getters and setters
}