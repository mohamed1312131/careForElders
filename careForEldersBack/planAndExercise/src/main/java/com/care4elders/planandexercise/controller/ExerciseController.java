package com.care4elders.planandexercise.controller;

import com.care4elders.planandexercise.DTO.ExerciseDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.exception.ServiceUnavailableException;
import com.care4elders.planandexercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ExerciseController.java
@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping("/create")
    public ResponseEntity<?> createExercise(
            @Valid @RequestBody ExerciseDTO exerciseDTO,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            Exercise createdExercise = exerciseService.createExercise(exerciseDTO, doctorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServiceUnavailableException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }

    @GetMapping("/getAllExercises")
    public ResponseEntity<?> getAllExercises() {
        try {
            List<Exercise> exercises = exerciseService.getAllExercises();
            return ResponseEntity.ok(exercises);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServiceUnavailableException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }


}