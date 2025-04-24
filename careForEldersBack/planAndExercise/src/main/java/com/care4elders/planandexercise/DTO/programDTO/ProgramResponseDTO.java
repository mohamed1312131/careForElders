package com.care4elders.planandexercise.DTO.programDTO;

import java.time.LocalDateTime;
import java.util.List;

import com.care4elders.planandexercise.DTO.programExerciseDTO.ProgramExerciseResponseDTO;
import com.care4elders.planandexercise.DTO.userDTO.DoctorInfoDTO;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProgramResponseDTO {
    private String programId;
    private String name;
    private String description;
    private Integer durationDays;
    private LocalDateTime createdAt;
    private DoctorInfoDTO doctor;
    private List<ProgramExerciseResponseDTO> exercises;
}