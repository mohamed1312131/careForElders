package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseRequestDTO;
import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseResponseDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseResponseDTO createExercise(ExerciseRequestDTO exerciseRequest) {
        Exercise exercise = Exercise.builder()
                .name(exerciseRequest.getName())
                .description(exerciseRequest.getDescription())
                .videoUrl(exerciseRequest.getVideoUrl())
                .imageUrls(exerciseRequest.getImageUrls())
                .type(exerciseRequest.getType())
                .durationMinutes(exerciseRequest.getDurationMinutes())
                .caloriesBurned(exerciseRequest.getCaloriesBurned())
                .equipmentRequired(exerciseRequest.getEquipmentRequired())
                .difficultyLevel(exerciseRequest.getDifficultyLevel())
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);
        return mapToResponseDTO(savedExercise);
    }

    private ExerciseResponseDTO mapToResponseDTO(Exercise exercise) {
        return ExerciseResponseDTO.builder()
                .exerciseId(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .videoUrl(exercise.getVideoUrl())
                .imageUrls(exercise.getImageUrls())
                .type(exercise.getType())
                .durationMinutes(exercise.getDurationMinutes())
                .caloriesBurned(exercise.getCaloriesBurned())
                .equipmentRequired(exercise.getEquipmentRequired())
                .difficultyLevel(exercise.getDifficultyLevel())
                .build();
    }
}