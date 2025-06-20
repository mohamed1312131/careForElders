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
public class AppointmentDTO {
    private String id;
    private String elderId;
    private String professionalId;
    private String professionalName;
    private String specialty;
    private LocalDateTime appointmentTime;
    private String location;
    private String notes;
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }
    public String getProfessionalId() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId = professionalId; }
    public String getProfessionalName() { return professionalName; }
    public void setProfessionalName(String professionalName) { this.professionalName = professionalName; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String id;
        private String elderId;
        private String professionalId;
        private String professionalName;
        private String specialty;
        private LocalDateTime appointmentTime;
        private String location;
        private String notes;
        private String status;
        public Builder id(String id) { this.id = id; return this; }
        public Builder elderId(String elderId) { this.elderId = elderId; return this; }
        public Builder professionalId(String professionalId) { this.professionalId = professionalId; return this; }
        public Builder professionalName(String professionalName) { this.professionalName = professionalName; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder appointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public AppointmentDTO build() {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setId(id);
            dto.setElderId(elderId);
            dto.setProfessionalId(professionalId);
            dto.setProfessionalName(professionalName);
            dto.setSpecialty(specialty);
            dto.setAppointmentTime(appointmentTime);
            dto.setLocation(location);
            dto.setNotes(notes);
            dto.setStatus(status);
            return dto;
        }
    }
}

