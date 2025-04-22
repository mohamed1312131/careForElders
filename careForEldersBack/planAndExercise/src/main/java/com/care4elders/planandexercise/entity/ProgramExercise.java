package com.care4elders.planandexercise.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class ProgramExercise {
    @NotBlank
    private String exerciseId;

    @Min(1)
    private Integer orderInProgram;

    @Min(1)
    private Integer repetitions;

    @Min(0)
    private Integer restTimeSeconds;

    private String specialInstructions;
}
