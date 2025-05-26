package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service

public class DoctorValidationService {
    private final RestTemplate restTemplate;

    public DoctorValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateDoctor(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://USER-SERVICE/users/{id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"DOCTOR".equalsIgnoreCase(doctor.getRole())) {
                throw new IllegalArgumentException("Invalid doctor ID or role");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Doctor validation failed: " + e.getMessage());
        }
    }
}
