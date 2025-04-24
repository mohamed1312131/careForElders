package com.care4elders.planandexercise.DTO.ExerciseCompletion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCompletionRequestDTO {
    @NotBlank
    private String patientId;

    @NotBlank
    private String programId;

    @NotBlank
    private String exerciseId;

    @Min(1) @Max(5)
    private Integer difficultyRating;

    private String feedback;
}