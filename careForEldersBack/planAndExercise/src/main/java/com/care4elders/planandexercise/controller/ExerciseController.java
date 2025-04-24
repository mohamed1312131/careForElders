package com.care4elders.planandexercise.controller;

import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseRequestDTO;
import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseResponseDTO;
import com.care4elders.planandexercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponseDTO> createExercise(
            @Valid @RequestBody ExerciseRequestDTO exerciseRequest
    ) {
        ExerciseResponseDTO response = exerciseService.createExercise(exerciseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}