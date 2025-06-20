package com.care4elders.paramedicalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamedicalProfessionalDTO {
    private String id;
    private String name;
    private String specialty;
    private String contactInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double distance;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String id;
        private String name;
        private String specialty;
        private String contactInfo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Double distance;
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder contactInfo(String contactInfo) { this.contactInfo = contactInfo; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder distance(Double distance) { this.distance = distance; return this; }
        public ParamedicalProfessionalDTO build() {
            ParamedicalProfessionalDTO dto = new ParamedicalProfessionalDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setSpecialty(specialty);
            dto.setContactInfo(contactInfo);
            dto.setCreatedAt(createdAt);
            dto.setUpdatedAt(updatedAt);
            dto.setDistance(distance);
            return dto;
        }
    }
}

