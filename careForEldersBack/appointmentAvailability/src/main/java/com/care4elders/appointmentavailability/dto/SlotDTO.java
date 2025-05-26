package com.care4elders.appointmentavailability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SlotDTO {
    private String doctorId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Integer slotDuration;


}
