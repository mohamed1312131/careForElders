package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.entity.Appointment;
import com.care4elders.medicalrecord.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedAppointments(AppointmentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Seeding mock appointment data...");

                List<Appointment> appointments = List.of(
                        new Appointment(null, "user123", "doctor123", "nurse456",
                                LocalDateTime.of(2024, 11, 26, 9, 0), "Open Access", "completed"),
                        new Appointment(null, "user123", "doctor123", "nurse456",
                                LocalDateTime.of(2024, 12, 12, 9, 0), "Root Canal Prep", "upcoming"),
                        new Appointment(null, "user123", "doctor789", "nurse111",
                                LocalDateTime.of(2025, 1, 10, 14, 0), "Cavity Filling", "upcoming")
                );

                repository.saveAll(appointments);
                System.out.println("✅ Mock appointments seeded.");
            } else {
                System.out.println("Appointments already exist, skipping seeding.");
            }
        };
    }
}
