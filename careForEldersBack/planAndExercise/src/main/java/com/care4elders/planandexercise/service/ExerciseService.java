package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.ExerciseDTO;
import com.care4elders.planandexercise.DTO.UserDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.exception.ServiceUnavailableException;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate;

    public Exercise createExercise(ExerciseDTO exerciseDTO, String doctorId) {
        validateDoctor(doctorId);

        Exercise exercise = new Exercise();
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setImageUrl(exerciseDTO.getImageUrl());
        exercise.setVideoTutorialUrl(exerciseDTO.getVideoTutorialUrl());
        exercise.setDefaultDurationMinutes(exerciseDTO.getDefaultDurationMinutes());
        exercise.setCategories(exerciseDTO.getCategories());
        exercise.setDifficultyLevel(exerciseDTO.getDifficultyLevel());
        exercise.setEquipmentNeeded(exerciseDTO.getEquipmentNeeded());
        exercise.setTargetMuscleGroup(exerciseDTO.getTargetMuscleGroup());
        exercise.setCreatedBy(doctorId);
        exercise.setStatus("ACTIVE");
        exercise.setCreatedAt(LocalDateTime.now());

        return exerciseRepository.save(exercise);
    }

    private void validateDoctor(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://USER-SERVICE/users/{Id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
                throw new EntityNotFoundException("Doctor not found or invalid role");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Doctor not found with ID: " + doctorId);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("User service unavailable");
        }
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }


}