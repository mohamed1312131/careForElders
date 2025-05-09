package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.entity.Disponibilite;
import com.care4elders.appointmentavailability.repository.IDisponibiliteRepository;
import com.care4elders.appointmentavailability.dto.UserDTO;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class DispoService implements IDispoService {

    private final IDisponibiliteRepository disponibiliteRepo;
    private final RestTemplate restTemplate;

    @Override
    public Disponibilite createDisponibilite(Disponibilite dispo) {
        validateDoctor(dispo.getDoctorId());
        return disponibiliteRepo.save(dispo);
    }

    @Override
    public List<Disponibilite> getAllDisponibilites() {
        return disponibiliteRepo.findAll();
    }

    public List<Disponibilite> getDisponibilitesByDoctor(String doctorId) {
        return disponibiliteRepo.findByDoctorId(doctorId);
    }

    private void validateDoctor(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://USER-SERVICE/users/{id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"DOCTOR".equalsIgnoreCase(doctor.getRole())) {
                throw new NotFoundException("Doctor not found or invalid role");
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Doctor not found with ID: " + doctorId);
        } catch (ResourceAccessException ex) {
            throw new RuntimeException("User service unavailable");
        }
    }

}
