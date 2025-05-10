package com.care4elders.planandexercise.controller;

import com.care4elders.planandexercise.DTO.ExerciseDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.exception.ServiceUnavailableException;
import com.care4elders.planandexercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:4200") // Consider making this configurable
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createExercise(
            @Valid @RequestPart("exerciseDTO") ExerciseDTO exerciseDTO,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(name = "videoFile", required = false) MultipartFile videoFile,
            @RequestHeader("X-User-ID") String doctorId) { // Assuming X-User-ID is the doctor's ID
        try {
            logger.info("Received request to create exercise: {} with image: {} and video: {}",
                    exerciseDTO.getName(),
                    imageFile != null ? imageFile.getOriginalFilename() : "No image",
                    videoFile != null ? videoFile.getOriginalFilename() : "No video");

            Exercise createdExercise = exerciseService.createExercise(exerciseDTO, imageFile, videoFile, doctorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
        } catch (EntityNotFoundException ex) {
            logger.warn("Entity not found while creating exercise: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServiceUnavailableException ex) {
            logger.error("Service unavailable while creating exercise: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        } catch (IOException ex) {
            logger.error("File upload failed while creating exercise: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid argument while creating exercise: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while creating exercise: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/getAllExercises")
    public ResponseEntity<?> getAllExercises() {
        try {
            List<Exercise> exercises = exerciseService.getAllExercises();
            return ResponseEntity.ok(exercises);
        } /*catch (EntityNotFoundException ex) { // getAllExercises typically shouldn't throw this unless no exercises is an error
            logger.warn("No exercises found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }*/ catch (ServiceUnavailableException ex) { // This might occur if there's a dependency service issue
            logger.error("Service unavailable while fetching all exercises: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while fetching all exercises: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}