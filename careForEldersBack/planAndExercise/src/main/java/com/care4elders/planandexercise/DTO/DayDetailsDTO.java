package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayDetailsDTO {
    private int dayNumber;
    private boolean locked;
    private boolean completed;
    private LocalDateTime completionDate;
    private int totalDuration;
    private List<ExerciseDTO> exercises;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    // Getters and setters
}