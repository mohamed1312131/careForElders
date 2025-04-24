package com.care4elders.planandexercise.DTO.addProgramToUser;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramAssignmentRequestDTO {
    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Program ID is required")
    private String programId;
}