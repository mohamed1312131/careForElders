package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.entity.Appointment;
import com.care4elders.medicalrecord.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    // Insert static appointments only once (if they don't exist)
    @GetMapping("/patient/{userId}")
    public List<Appointment> getAppointments(@PathVariable String userId) {
        List<Appointment> existing = service.getAppointmentsByPatient(userId);
        if (existing.isEmpty()) {
            Appointment appt1 = new Appointment(
                    null,
                    userId,
                    "doctor123",
                    "nurse456",
                    LocalDateTime.of(2024, 11, 26, 9, 0),
                    "Open Access",
                    "completed"
            );

            Appointment appt2 = new Appointment(
                    null,
                    userId,
                    "doctor123",
                    "nurse456",
                    LocalDateTime.of(2024, 12, 12, 9, 0),
                    "Root Canal Prep",
                    "upcoming"
            );

            service.save(appt1);
            service.save(appt2);
        }

        return service.getAppointmentsByPatient(userId);
    }
}
