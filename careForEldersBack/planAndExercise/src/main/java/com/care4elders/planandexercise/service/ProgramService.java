package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentRequestDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentResponseDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramDetailsDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramResponseDTO;
import com.care4elders.planandexercise.DTO.programExerciseDTO.ProgramExerciseResponseDTO;
import com.care4elders.planandexercise.DTO.userDTO.DoctorInfoDTO;
import com.care4elders.planandexercise.DTO.userDTO.UserDTO;
import com.care4elders.planandexercise.entity.*;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import com.care4elders.planandexercise.repository.PatientProgramRepository;
import com.care4elders.planandexercise.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate;
    private final PatientProgramRepository patientProgramRepository;

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

        if (patient == null || !"PATIENT".equals(patient.getRole())) {
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
}