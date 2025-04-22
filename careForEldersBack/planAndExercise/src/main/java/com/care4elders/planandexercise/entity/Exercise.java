package com.care4elders.planandexercise.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.hibernate.validator.constraints.URL;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Singular;

@Document(collection = "exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    private String id;

    @NotBlank
    @Indexed
    private String name;

    private String description;

    @URL
    private String videoUrl;

    @Singular
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