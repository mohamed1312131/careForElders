package com.care4elders.planandexercise.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Document(collection = "patient_programs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProgram {
    @Id
    private String id;

    @NotBlank
    @Indexed
    private String patientId;

    @NotBlank
    @Indexed
    private String programId;

    @NotNull
    private LocalDateTime assignedDate;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private ProgramStatus status = ProgramStatus.ACTIVE;
}