package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.PatientProgramHistoryDTO.PatientProgramHistoryDTO;
import com.care4elders.planandexercise.DTO.PatientProgramHistoryDTO.ProgramExerciseStatusDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentRequestDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentResponseDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramDetailsDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramResponseDTO;
import com.care4elders.planandexercise.DTO.programExerciseDTO.ProgramExerciseResponseDTO;
import com.care4elders.planandexercise.DTO.userDTO.DoctorInfoDTO;
import com.care4elders.planandexercise.DTO.userDTO.UserDTO;
import com.care4elders.planandexercise.entity.*;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate;
    private final PatientProgramRepository patientProgramRepository;
    private final ExerciseCompletionRepository completionRepository;
    private final NotificationService notificationService;
    public ProgramResponseDTO createProgram(ProgramRequestDTO programRequest) {
        // Fetch and validate doctor from user-service
        UserDTO doctor = restTemplate.getForObject(
                "http://USER-SERVICE/users/{doctorId}",
                UserDTO.class,
                programRequest.getDoctorId()
        );

        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            throw new EntityNotFoundException("Doctor not found or invalid role");
        }

        // Build program exercises
        List<ProgramExercise> exercises = programRequest.getExercises().stream()
                .map(exDto -> {
                    Exercise exercise = exerciseRepository.findById(exDto.getExerciseId())
                            .orElseThrow(() -> new EntityNotFoundException("Exercise not found with ID: " + exDto.getExerciseId()));

                    return ProgramExercise.builder()
                            .exerciseId(exercise.getId())
                            .orderInProgram(exDto.getOrderInProgram())
                            .repetitions(exDto.getRepetitions())
                            .restTimeSeconds(exDto.getRestTimeSeconds())
                            .specialInstructions(exDto.getSpecialInstructions())
                            .build();
                }).collect(Collectors.toList());

        // Create and save program
        Program program = Program.builder()
                .name(programRequest.getName())
                .description(programRequest.getDescription())
                .exercises(exercises)
                .durationDays(programRequest.getDurationDays())
                .createdByDoctorId(programRequest.getDoctorId())
                .build();

        Program savedProgram = programRepository.save(program);

        return mapToResponseDTO(savedProgram, doctor);
    }

    private ProgramResponseDTO mapToResponseDTO(Program program, UserDTO doctor) {
        return ProgramResponseDTO.builder()
                .programId(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .durationDays(program.getDurationDays())
                .createdAt(program.getCreatedAt())
                .doctor(new DoctorInfoDTO(
                        doctor.getId(),
                        doctor.getFirstName(),
                        doctor.getSpecialization()
                ))
                .exercises(program.getExercises().stream()
                        .map(ex -> {
                            Exercise exercise = exerciseRepository.findById(ex.getExerciseId())
                                    .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

                            return ProgramExerciseResponseDTO.builder()
                                    .exerciseId(ex.getExerciseId())
                                    .exerciseName(exercise.getName())
                                    .orderInProgram(ex.getOrderInProgram())
                                    .repetitions(ex.getRepetitions())
                                    .restTimeSeconds(ex.getRestTimeSeconds())
                                    .specialInstructions(ex.getSpecialInstructions())
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }
    public ProgramAssignmentResponseDTO assignProgramToPatient(ProgramAssignmentRequestDTO request) {
        // Validate patient exists in user-service
        UserDTO patient = restTemplate.getForObject(
                "http://USER-SERVICE/users/{patientId}",
                UserDTO.class,
                request.getPatientId()
        );

        if (patient == null || !"NORMAL_USER".equals(patient.getRole())) {
            throw new EntityNotFoundException("Patient not found or invalid role");
        }

        // Validate program exists
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found with ID: " + request.getProgramId()));

        // Create assignment
        PatientProgram assignment = PatientProgram.builder()
                .patientId(request.getPatientId())
                .programId(request.getProgramId())
                .assignedDate(LocalDateTime.now())
                .status(ProgramStatus.ACTIVE)
                .build();

        PatientProgram savedAssignment = patientProgramRepository.save(assignment);
        notificationService.sendProgramAssignedNotification(
                request.getPatientId(),
                program
        );
        return mapToResponseDTO(savedAssignment, program);
    }

    private ProgramAssignmentResponseDTO mapToResponseDTO(PatientProgram assignment, Program program) {
        return ProgramAssignmentResponseDTO.builder()
                .assignmentId(assignment.getId())
                .patientId(assignment.getPatientId())
                .programId(assignment.getProgramId())
                .assignedDate(assignment.getAssignedDate())
                .status(assignment.getStatus())
                .programDetails(ProgramDetailsDTO.builder()
                        .programId(program.getId())
                        .name(program.getName())
                        .description(program.getDescription())
                        .durationDays(program.getDurationDays())
                        .build())
                .build();
    }
    public List<ProgramAssignmentResponseDTO> getCurrentPatientPrograms(String patientId) {
        // Validate patient exists
        UserDTO patient = restTemplate.getForObject(
                "http://USER-SERVICE/users/{patientId}",
                UserDTO.class,
                patientId
        );

        if (patient == null || !"NORMAL_USER".equals(patient.getRole())) {
            throw new EntityNotFoundException("Patient not found");
        }

        List<PatientProgram> programs = patientProgramRepository.findByPatientIdAndStatus(patientId, ProgramStatus.ACTIVE);

        return programs.stream()
                .map(pp -> {
                    Program program = programRepository.findById(pp.getProgramId())
                            .orElseThrow(() -> new EntityNotFoundException("Program not found"));
                    return mapToResponseDTO(pp, program);
                })
                .collect(Collectors.toList());
    }
    // PatientProgramService.java
    public ProgramAssignmentResponseDTO completeProgram(String patientId, String programId) {
        PatientProgram program = patientProgramRepository.findByPatientIdAndProgramId(patientId, programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not assigned to patient"));

        // Check if all exercises are completed
        Program programDetails = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        long totalExercises = programDetails.getExercises().size();
        long completedExercises = completionRepository.countByPatientProgramId(program.getId());

        if (completedExercises < totalExercises) {
            throw new BusinessException("Cannot complete program - " + (totalExercises - completedExercises) + " exercises remaining");
        }

        program.setStatus(ProgramStatus.COMPLETED);
        program.setCompletionDate(LocalDateTime.now());
        PatientProgram updated = patientProgramRepository.save(program);
        UserDTO patient = restTemplate.getForObject(
                "http://USER-SERVICE/users/{patientId}",
                UserDTO.class,
                patientId
        );
        notificationService.sendProgramCompletedNotification(
                programDetails.getCreatedByDoctorId(),
                patient.getFirstName() + " " + patient.getLastName(),
                programDetails.getName()
        );
        return mapToResponseDTO(updated, programDetails);
    }

    public List<PatientProgramHistoryDTO> getPatientProgramHistory(String patientId) {
        // Validate patient exists
        UserDTO patient = restTemplate.getForObject(
                "http://USER-SERVICE/users/{patientId}",
                UserDTO.class,
                patientId
        );
        if (patient == null || !"NORMAL_USER".equals(patient.getRole())) {
            throw new EntityNotFoundException("Patient not found");
        }

        List<PatientProgram> patientPrograms = patientProgramRepository.findByPatientId(patientId);

        return patientPrograms.stream()
                .map(pp -> {
                    Program program = programRepository.findById(pp.getProgramId())
                            .orElseThrow(() -> new EntityNotFoundException("Program not found"));

                    List<ExerciseCompletion> completions = completionRepository.findByPatientProgramId(pp.getId());

                    return buildHistoryDTO(pp, program, completions);
                })
                .collect(Collectors.toList());
    }

    private PatientProgramHistoryDTO buildHistoryDTO(
            PatientProgram patientProgram,
            Program program,
            List<ExerciseCompletion> completions
        ) {
        Map<String, ExerciseCompletion> completionMap = completions.stream()
                .collect(Collectors.toMap(
                        ExerciseCompletion::getExerciseId,
                        Function.identity()
                ));

        List<ProgramExerciseStatusDTO> exerciseStatuses = program.getExercises().stream()
                .map(pe -> {
                    Exercise exercise = exerciseRepository.findById(pe.getExerciseId())
                            .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

                    ExerciseCompletion completion = completionMap.get(pe.getExerciseId());

                    return ProgramExerciseStatusDTO.builder()
                            .exerciseId(pe.getExerciseId())
                            .exerciseName(exercise.getName())
                            .orderInProgram(pe.getOrderInProgram())
                            .completed(completion != null)
                            .completionDate(completion != null ? completion.getCompletedDate() : null)
                            .difficultyRating(completion != null ? completion.getDifficultyRating() : null)
                            .feedback(completion != null ? completion.getFeedback() : null)
                            .build();
                }).collect(Collectors.toList());

        int completedCount = (int) exerciseStatuses.stream()
                .filter(ProgramExerciseStatusDTO::isCompleted)
                .count();
        int total = exerciseStatuses.size();
        double percentage = total > 0 ? (completedCount * 100.0) / total : 0.0;

        return PatientProgramHistoryDTO.builder()
                .programId(program.getId())
                .programName(program.getName())
                .status(patientProgram.getStatus())
                .assignedDate(patientProgram.getAssignedDate())
                .completionDate(patientProgram.getCompletionDate())
                .exercises(exerciseStatuses)
                .completedExercises(completedCount)
                .totalExercises(total)
                .completionPercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

}