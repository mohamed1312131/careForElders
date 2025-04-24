package com.care4elders.planandexercise.DTO.exerciseDTO;

import com.care4elders.planandexercise.entity.ExerciseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequestDTO {
    @NotBlank
    private String name;

    private String description;

    @URL
    private String videoUrl;

    private List<@URL String> imageUrls;

    @NotNull
    private ExerciseType type;

    @Min(1)
    private Integer durationMinutes;

    @Min(0)
    private Integer caloriesBurned;

    private String equipmentRequired;

    @NotBlank
    private String difficultyLevel;
}