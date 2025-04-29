package com.care4elders.planandexercise.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayCompletionDTO {
    @Min(1)
    private int dayNumber;

    @Min(1)
    private int actualDurationMinutes;

    private String patientNotes;

    @Min(1) @Max(5)
    private int perceivedDifficulty;

    // Getters + Setters
}