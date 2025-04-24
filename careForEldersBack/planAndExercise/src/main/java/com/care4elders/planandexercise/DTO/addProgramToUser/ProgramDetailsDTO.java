package com.care4elders.planandexercise.DTO.addProgramToUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramDetailsDTO {
    private String programId;
    private String name;
    private String description;
    private Integer durationDays;
}