package com.care4elders.paramedicalcare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;

@Document(collection = "paramedical_professionals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamedicalProfessional {
    @Id
    private String id;
    private String name;
    private String specialty;
    private String contactInfo;
    private GeoJsonPoint location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public GeoJsonPoint getLocation() { return location; }
    public void setLocation(GeoJsonPoint location) { this.location = location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String id;
        private String name;
        private String specialty;
        private String contactInfo;
        private GeoJsonPoint location;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder contactInfo(String contactInfo) { this.contactInfo = contactInfo; return this; }
        public Builder location(GeoJsonPoint location) { this.location = location; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ParamedicalProfessional build() {
            ParamedicalProfessional p = new ParamedicalProfessional();
            p.setId(id);
            p.setName(name);
            p.setSpecialty(specialty);
            p.setContactInfo(contactInfo);
            p.setLocation(location);
            p.setCreatedAt(createdAt);
            p.setUpdatedAt(updatedAt);
            return p;
        }
    }
}

