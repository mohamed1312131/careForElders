package com.care4elders.appointmentavailability.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class DisponibiliteDTO {
    private String doctorId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Integer slotDuration;
}
