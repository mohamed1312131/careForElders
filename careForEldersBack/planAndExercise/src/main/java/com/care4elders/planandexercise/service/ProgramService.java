package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.ProgramExerciseResponseDTO;
import com.care4elders.planandexercise.DTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.ProgramResponseDTO;
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.entity.Program;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.repository.DoctorRepository;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import com.care4elders.planandexercise.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.care4elders.planandexercise.entity.ProgramExercise;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;
    private final DoctorRepository doctorRepository;

    public ProgramResponseDTO createProgram(ProgramRequestDTO programRequest) {
        // Validate doctor exists
        doctorRepository.findById(programRequest.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + programRequest.getDoctorId()));

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

        return mapToResponseDTO(savedProgram);
    }

    private ProgramResponseDTO mapToResponseDTO(Program program) {
        return ProgramResponseDTO.builder()
                .programId(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .durationDays(program.getDurationDays())
                .createdAt(program.getCreatedAt())
                .doctorId(program.getCreatedByDoctorId())
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
}