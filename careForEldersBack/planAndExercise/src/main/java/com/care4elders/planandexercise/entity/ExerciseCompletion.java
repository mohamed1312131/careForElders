package com.care4elders.planandexercise.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "exercise_completions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCompletion {
    @Id
    private String id;

    @NotBlank
    @Indexed
    private String patientProgramId;

    @NotBlank
    private String programExerciseId;

    @NotNull
    private LocalDateTime completedDate;

    private String feedback;

    @Min(1) @Max(5)
    private Integer difficultyRating;

    @Builder.Default
    private boolean completed = false;
}