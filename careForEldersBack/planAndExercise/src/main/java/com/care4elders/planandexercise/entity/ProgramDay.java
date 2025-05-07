package com.care4elders.planandexercise.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramDay {
    private String id;
    private String programId;
    private int dayNumber;
    private List<String> exerciseIds;
    private int totalDurationMinutes;
    private String instructions;
    private int order;
    private boolean restDay;
    private int warmUpMinutes;
    private int coolDownMinutes;
    private String notesForPatient;
    private String notesForDoctor;
}

