package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProgramDayDetailsDTO {
    private String id;
    private int dayNumber;
    private boolean restDay;
    private int totalDurationMinutes;
    private String instructions;
    private int warmUpMinutes;
    private int coolDownMinutes;
    private String notesForPatient;
    private String notesForDoctor;
    private List<ExerciseDTO> exercises;
}
