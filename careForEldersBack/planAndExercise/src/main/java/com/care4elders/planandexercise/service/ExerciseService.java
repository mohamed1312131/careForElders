package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.ExerciseCompletion.ExerciseCompletionDTO;
import com.care4elders.planandexercise.DTO.ExerciseCompletion.ExerciseCompletionRequestDTO;
import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseRequestDTO;
import com.care4elders.planandexercise.DTO.exerciseDTO.ExerciseResponseDTO;
import com.care4elders.planandexercise.DTO.userDTO.UserDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.entity.ExerciseCompletion;
import com.care4elders.planandexercise.entity.PatientProgram;
import com.care4elders.planandexercise.entity.Program;
import com.care4elders.planandexercise.exception.BusinessException;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.repository.ExerciseCompletionRepository;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import com.care4elders.planandexercise.repository.PatientProgramRepository;
import com.care4elders.planandexercise.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseCompletionRepository completionRepository;
    private final PatientProgramRepository patientProgramRepository;
    private final ProgramRepository programRepository;
    private final RestTemplate restTemplate;
    private final NotificationService notificationService;

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
    public ExerciseCompletionDTO trackExerciseCompletion(ExerciseCompletionRequestDTO request) {
        // Validate program assignment
        PatientProgram program = patientProgramRepository.findByPatientIdAndProgramId(request.getPatientId(), request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not assigned to patient"));

        // Validate exercise belongs to program
        Program programDetails = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        boolean exerciseInProgram = programDetails.getExercises().stream()
                .anyMatch(e -> e.getExerciseId().equals(request.getExerciseId()));

        if (!exerciseInProgram) {
            throw new BusinessException("Exercise not part of this program");
        }

        ExerciseCompletion completion = ExerciseCompletion.builder()
                .patientProgramId(program.getId())
                .exerciseId(request.getExerciseId())
                .completedDate(LocalDateTime.now())
                .feedback(request.getFeedback())
                .difficultyRating(request.getDifficultyRating())
                .completed(true)
                .build();

        ExerciseCompletion saved = completionRepository.save(completion);
        UserDTO patient = restTemplate.getForObject(
                "http://USER-SERVICE/users/{patientId}",
                UserDTO.class,
                request.getPatientId()
        );
        notificationService.sendExerciseCompletedNotification(
                programDetails.getCreatedByDoctorId(),
                patient.getFirstName() + " " + patient.getLastName(),
                exercise.getName()
        );
        return mapToDTO(saved);
    }

    private ExerciseCompletionDTO mapToDTO(ExerciseCompletion completion) {
        return ExerciseCompletionDTO.builder()
                .id(completion.getId())
                .exerciseId(completion.getExerciseId())
                .completedDate(completion.getCompletedDate())
                .feedback(completion.getFeedback())
                .difficultyRating(completion.getDifficultyRating())
                .build();
    }
}