package com.care4elders.paramedicalcare.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfessionalRequest {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Specialty must be between 2 and 50 characters")
    private String specialty;

    @Size(min = 5, max = 200, message = "Contact info must be between 5 and 200 characters")
    private String contactInfo;

    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasSpecialty() {
        return specialty != null && !specialty.trim().isEmpty();
    }

    public boolean hasContactInfo() {
        return contactInfo != null && !contactInfo.trim().isEmpty();
    }
}
