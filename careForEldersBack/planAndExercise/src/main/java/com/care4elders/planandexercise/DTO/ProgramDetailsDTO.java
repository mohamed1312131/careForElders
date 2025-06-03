package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramDetailsDTO {
    private String id;
    private String name;
    private String description;
    private String programCategory;
    private String programImage;
    private String status;
    private int durationWeeks;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private List<ProgramDayDetailsDTO> days;

    // Getters and setters
}