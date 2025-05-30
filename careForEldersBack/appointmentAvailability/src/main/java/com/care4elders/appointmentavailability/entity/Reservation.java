package com.care4elders.appointmentavailability.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;
@Data

public class Reservation {
    @Id
    private String id;
    private LocalDate date;
    private LocalTime heure;
    private LocalTime endTime; // Add this field
    private StatutReservation statut;
    private String userId;
    private String doctorId;
    private TypeReservation reservationType;

    // Conditional validation (handled in Service)
    private String meetingLink;  // Required if EN_LIGNE
    private String location;    // Required if PRESENTIEL
}


