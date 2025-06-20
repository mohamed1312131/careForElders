package com.care4elders.planandexercise.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramStatisticsDTO {
    private long totalAssignments;
    private long completedAssignments;
    private long activeAssignments;

    // Constructor, getters and setters

}