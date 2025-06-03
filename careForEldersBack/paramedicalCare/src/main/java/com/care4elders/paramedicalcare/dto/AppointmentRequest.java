package com.care4elders.paramedicalcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "Elder ID is required")
    private String elderId;

    @NotBlank(message = "Professional ID is required")
    private String professionalId;

    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;

    private String location;
    private String notes;
}
