package com.care4elders.planandexercise.DTO.AddExerciseToProgramRequest;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddExerciseToProgramRequestDTO {
    @NotBlank(message = "Exercise ID is required")
    private String exerciseId;

    @NotNull
    @Positive(message = "Order must be positive")
    private Integer orderInProgram;

    @NotNull
    @Positive(message = "Repetitions must be positive")
    private Integer repetitions;

    @NotNull
    @PositiveOrZero(message = "Rest time cannot be negative")
    private Integer restTimeSeconds;

    private String specialInstructions;
}