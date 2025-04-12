package com.care4elders.appointmentavailability.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;
@Data

public class Reservation {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private LocalDate date;
    private LocalTime heure;

    // @Enumerated(EnumType.STRING)
    private StatutReservation statut;
    private String userId;


    private String soignantId;     // ID of the caregiver (user with role "soignant")
    private String normalUserId;   // ID of the normal user (patient)



}