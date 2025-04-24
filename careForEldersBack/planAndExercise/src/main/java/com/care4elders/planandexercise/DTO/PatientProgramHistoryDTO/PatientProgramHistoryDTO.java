package com.care4elders.planandexercise.DTO.PatientProgramHistoryDTO;

import com.care4elders.planandexercise.entity.ProgramStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PatientProgramHistoryDTO {
    private String programId;
    private String programName;
    private ProgramStatus status;
    private LocalDateTime assignedDate;
    private LocalDateTime completionDate;
    private List<ProgramExerciseStatusDTO> exercises;
    private int completedExercises;
    private int totalExercises;
    private double completionPercentage;
}