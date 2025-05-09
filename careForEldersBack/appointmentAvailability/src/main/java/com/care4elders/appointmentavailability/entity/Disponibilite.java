package com.care4elders.appointmentavailability.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalTime;
@Document("disponibilites")
@Data
public class Disponibilite {
    @Id


    private String id;

    private String doctorId;

    private DayOfWeek jour;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    //@ManyToOne
    //  @JoinColumn(name = "medecin_id")
    // private Medecin medecin;
}
