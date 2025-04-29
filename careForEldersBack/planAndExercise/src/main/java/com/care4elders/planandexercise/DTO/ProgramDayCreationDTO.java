package com.care4elders.planandexercise.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramDayCreationDTO {
    private int dayNumber;
    private List<String> exerciseIds;
    private boolean restDay;
    private String instructions;
    private int warmUpMinutes;
    private int coolDownMinutes;
    private String notesForPatient;
}
