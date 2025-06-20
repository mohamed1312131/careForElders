package com.care4elders.paramedicalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String id;
    private String elderId;
    private String professionalId;
    private String professionalName;
    private String specialty;
    private LocalDateTime appointmentTime;
    private String location;
    private String notes;
    private String status;
}
