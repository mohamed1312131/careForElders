package com.care4elders.planandexercise.DTO.addProgramToUser;

import com.care4elders.planandexercise.entity.ProgramStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProgramAssignmentResponseDTO {
    private String assignmentId;
    private String patientId;
    private String programId;
    private LocalDateTime assignedDate;
    private ProgramStatus status;
    private
    ProgramDetailsDTO programDetails;
}