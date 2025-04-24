package com.care4elders.planandexercise.DTO.exerciseDTO;

import com.care4elders.planandexercise.entity.ExerciseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExerciseResponseDTO {
    private String exerciseId;
    private String name;
    private String description;
    private String videoUrl;
    private List<String> imageUrls;
    private ExerciseType type;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String equipmentRequired;
    private String difficultyLevel;
}