package com.care4elders.planandexercise.DTO.programExerciseDTO;


import lombok.Data;
import lombok.Builder;


@Data
@Builder
public class ProgramExerciseResponseDTO {
    private String exerciseId;
    private String exerciseName;
    private Integer orderInProgram;
    private Integer repetitions;
    private Integer restTimeSeconds;
    private String specialInstructions;
}
