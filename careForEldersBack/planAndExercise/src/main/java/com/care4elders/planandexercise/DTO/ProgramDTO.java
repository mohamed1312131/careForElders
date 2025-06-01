package com.care4elders.planandexercise.DTO;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramDTO {

    private String id; // Sent as readonly in the form

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String programCategory;

    private String programImage; // Optional field: base64 or image URL

    @NotNull
    private String status; // ENUM: DRAFT, PUBLISHED, ARCHIVED

    @NotNull
    @Min(1)
    private Integer durationWeeks;

    // Optional, used only during creation or if managing days in bulk
    private List<ProgramDayCreationDTO> days;
}
