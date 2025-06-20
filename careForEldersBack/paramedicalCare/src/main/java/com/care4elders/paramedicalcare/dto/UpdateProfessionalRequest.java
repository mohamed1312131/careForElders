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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String name;
        private String specialty;
        private String contactInfo;
        public Builder name(String name) { this.name = name; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder contactInfo(String contactInfo) { this.contactInfo = contactInfo; return this; }
        public UpdateProfessionalRequest build() {
            UpdateProfessionalRequest req = new UpdateProfessionalRequest();
            req.setName(name);
            req.setSpecialty(specialty);
            req.setContactInfo(contactInfo);
            return req;
        }
    }
}

