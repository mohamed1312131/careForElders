package com.care4elders.planandexercise.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramDTO {
    @NotBlank
    private String name;
    private String description;
    private String programCategory;
    private String programImage; // Base64 or URL
    private List<ProgramDayCreationDTO> days;
}