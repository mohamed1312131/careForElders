package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramListDTO {
    private String id;
    private String name;
    private String description;
    private String programCategory;
    private String status;
    private int durationWeeks;
    private LocalDateTime createdDate;
    private String createdBy;
    private int numberOfDays;
    private int numberOfExercises;
    private int numberOfPatients;
    private int totalDurationMinutes;

    // Add constructor, getters, and setters
}