package com.care4elders.planandexercise.DTO.programDTO;


import com.care4elders.planandexercise.DTO.programExerciseDTO.ProgramExerciseDTO;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRequestDTO {
    @NotBlank(message = "Program name is required")
    private String name;

    private String description;

    @NotNull
    @Positive(message = "Duration must be positive")
    private Integer durationDays;

    @NotEmpty(message = "Exercises are required")
    private List<ProgramExerciseDTO> exercises;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
}