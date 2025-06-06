package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.dto.DisponibiliteDTO;
import com.care4elders.appointmentavailability.dto.SlotDTO;
import com.care4elders.appointmentavailability.entity.Disponibilite;
import com.care4elders.appointmentavailability.entity.Reservation;
import com.care4elders.appointmentavailability.repository.IDisponibiliteRepository;
import com.care4elders.appointmentavailability.dto.UserDTO;

import com.care4elders.appointmentavailability.repository.IReservationRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor

public class DispoService {

    private final IDisponibiliteRepository repository;
    private final DoctorValidationService doctorValidationService;

    public Disponibilite createDisponibilite(DisponibiliteDTO dto) {
        // Validate doctor exists
        doctorValidationService.validateDoctor(dto.getDoctorId());

        Disponibilite disponibilite = new Disponibilite();
        disponibilite.setDoctorId(dto.getDoctorId());
        disponibilite.setDate(dto.getDate());
        disponibilite.setHeureDebut(dto.getHeureDebut());
        disponibilite.setHeureFin(dto.getHeureFin());
        disponibilite.setSlotDuration(dto.getSlotDuration() != null ? dto.getSlotDuration() : 30);

        if (!disponibilite.isValid()) {
            throw new IllegalArgumentException("Invalid availability time range");
        }

        return repository.save(disponibilite);
    }

    public List<Disponibilite> getByDoctorId(String doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    public Disponibilite getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Availability not found"));
    }

    public Disponibilite updateDisponibilite(String id, DisponibiliteDTO dto) {
        Disponibilite existing = getById(id);

        if (dto.getHeureDebut() != null) existing.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) existing.setHeureFin(dto.getHeureFin());
        if (dto.getSlotDuration() != null) existing.setSlotDuration(dto.getSlotDuration());

        if (!existing.isValid()) {
            throw new IllegalArgumentException("Invalid availability time range");
        }

        return repository.save(existing);
    }

    public void deleteDisponibilite(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Availability not found");
        }
        repository.deleteById(id);
    }

    public List<Disponibilite> getByDoctorAndDateRange(String doctorId, LocalDate start, LocalDate end) {
        return repository.findByDoctorIdAndDateBetween(doctorId, start, end);
    }
}