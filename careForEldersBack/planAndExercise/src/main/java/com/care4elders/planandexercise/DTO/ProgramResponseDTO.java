package com.care4elders.planandexercise.DTO;

import java.time.LocalDateTime;
import java.util.List;
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
    private String doctorId;
    private List<ProgramExerciseResponseDTO> exercises;
}
