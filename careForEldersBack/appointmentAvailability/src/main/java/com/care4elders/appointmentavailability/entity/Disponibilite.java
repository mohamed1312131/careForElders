package com.care4elders.appointmentavailability.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
@Document("disponibilites")
@Data
public class Disponibilite {
    @Id
    private String id;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotNull(message = "Date is required")
    private LocalDate date;  // Changed from DayOfWeek to LocalDate

    @NotNull(message = "Start time is required")
    private LocalTime heureDebut;

    @NotNull(message = "End time is required")
    private LocalTime heureFin;

    private int slotDuration = 30; // Default 30 minutes

   //  Optional: Add validation methods
    public boolean isValid() {
        return heureFin.isAfter(heureDebut) && slotDuration > 0;
    }

}
