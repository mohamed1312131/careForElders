package com.care4elders.planandexercise.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramDayDTO {
    private String id;
    @NotNull
    private Integer dayNumber;
    private List<String> exerciseIds = new ArrayList<>();
    private String instructions;
    private String notesForPatient;
    private String notesForDoctor;
    private boolean restDay;
    @Min(0)
    private int warmUpMinutes;
    @Min(0)
    private int coolDownMinutes;
}
