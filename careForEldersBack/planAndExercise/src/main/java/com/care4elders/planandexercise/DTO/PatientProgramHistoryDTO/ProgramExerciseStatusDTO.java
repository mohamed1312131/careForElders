package com.care4elders.planandexercise.DTO.PatientProgramHistoryDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProgramExerciseStatusDTO {
    private String exerciseId;
    private String exerciseName;
    private int orderInProgram;
    private boolean completed;
    private LocalDateTime completionDate;
    private Integer difficultyRating;
    private String feedback;
}