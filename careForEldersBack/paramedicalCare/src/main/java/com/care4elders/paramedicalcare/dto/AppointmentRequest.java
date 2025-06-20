package com.care4elders.paramedicalcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "Elder ID is required")
    private String elderId;
    @NotBlank(message = "Professional ID is required")
    private String professionalId;
    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;
    private String location;
    private String notes;

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

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String elderId;
        private String professionalId;
        private LocalDateTime appointmentTime;
        private String location;
        private String notes;
        public Builder elderId(String elderId) { this.elderId = elderId; return this; }
        public Builder professionalId(String professionalId) { this.professionalId = professionalId; return this; }
        public Builder appointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public AppointmentRequest build() {
            AppointmentRequest req = new AppointmentRequest();
            req.setElderId(elderId);
            req.setProfessionalId(professionalId);
            req.setAppointmentTime(appointmentTime);
            req.setLocation(location);
            req.setNotes(notes);
            return req;
        }
    }
}

