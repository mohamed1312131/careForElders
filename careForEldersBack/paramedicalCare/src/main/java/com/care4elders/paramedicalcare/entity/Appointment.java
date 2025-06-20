package com.care4elders.paramedicalcare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    private String id;
    private String elderId;
    private String professionalId;
    private LocalDateTime appointmentTime;
    private String location;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }
    public String getProfessionalId() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId = professionalId; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String id;
        private String elderId;
        private String professionalId;
        private LocalDateTime appointmentTime;
        private String location;
        private String notes;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder id(String id) { this.id = id; return this; }
        public Builder elderId(String elderId) { this.elderId = elderId; return this; }
        public Builder professionalId(String professionalId) { this.professionalId = professionalId; return this; }
        public Builder appointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Appointment build() {
            Appointment a = new Appointment();
            a.setId(id);
            a.setElderId(elderId);
            a.setProfessionalId(professionalId);
            a.setAppointmentTime(appointmentTime);
            a.setLocation(location);
            a.setNotes(notes);
            a.setStatus(status);
            a.setCreatedAt(createdAt);
            a.setUpdatedAt(updatedAt);
            return a;
        }
    }
}

