package com.care4elders.planandexercise.entity;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Singular;

@Document(collection = "programs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;


    @Singular
    private List<ProgramExercise> exercises;

    @Min(1)
    private Integer durationDays;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotBlank
    private String createdByDoctorId;

}