package com.care4elders.planandexercise.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDTO {
    private String id;
    @NotBlank
    private String name;
    private String description;
    private String imageUrl;
    private String videoTutorialUrl;
    @Min(1)
    private int defaultDurationMinutes;
    private List<String> categories;
    private String difficultyLevel;
    private List<String> equipmentNeeded;
    private String targetMuscleGroup;
    private String videoUrl;
    private int durationMinutes;
}