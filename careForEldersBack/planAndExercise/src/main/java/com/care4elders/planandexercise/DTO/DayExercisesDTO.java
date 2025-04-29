package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DayExercisesDTO {
    private int dayNumber;
    private List<ExerciseDTO> exercises;
    private int totalDuration;
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED
    private LocalDateTime startTime;
    private boolean locked;
    // Getters and setters
}