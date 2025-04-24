package com.care4elders.planandexercise.DTO.ExerciseCompletion;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExerciseCompletionDTO {
    private String id;
    private String exerciseId;
    private LocalDateTime completedDate;
    private Integer difficultyRating;
    private String feedback;
}