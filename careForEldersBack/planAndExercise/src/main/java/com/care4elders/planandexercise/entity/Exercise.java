package com.care4elders.planandexercise.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoTutorialUrl;
    private int defaultDurationMinutes;
    private List<String> categories; // e.g., [Cardio, Strength, Flexibility]
    private String difficultyLevel; // Beginner, Intermediate, Advanced
    private List<String> equipmentNeeded; // [Dumbbells, Mat, None]
    private String targetMuscleGroup;
    private int caloriesBurnedEstimate;
    private int recommendedRepetitions;
    private int recommendedSets;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy; // User ID from user-service
    private String status; // ACTIVE, ARCHIVED
    // Other fields if needed
}