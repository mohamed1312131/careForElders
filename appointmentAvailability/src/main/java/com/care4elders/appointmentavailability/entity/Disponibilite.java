package com.care4elders.appointmentavailability.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class Disponibilite {
    @Id

    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    //  @Enumerated(EnumType.STRING)
    private DayOfWeek jour;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    //@ManyToOne
    //  @JoinColumn(name = "medecin_id")
    // private Medecin medecin;
}
