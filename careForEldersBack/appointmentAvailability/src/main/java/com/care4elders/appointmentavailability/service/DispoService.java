package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.dto.DisponibiliteDTO;
import com.care4elders.appointmentavailability.entity.Disponibilite;
import com.care4elders.appointmentavailability.repository.IDisponibiliteRepository;
import com.care4elders.appointmentavailability.dto.UserDTO;
import com.care4elders.appointmentavailability.repository.IReservationRepository;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispoService {

    private final IDisponibiliteRepository repository;
    private final DoctorValidationService doctorValidationService;

    public Disponibilite createDisponibilite(DisponibiliteDTO dto) {
        // ✅ Log input for debugging
        log.info("Creating availability: doctorId={}, date={}, start={}, end={}",
                dto.getDoctorId(), dto.getDate(), dto.getHeureDebut(), dto.getHeureFin());

        // ✅ Validate doctor existence
        doctorValidationService.validateDoctor(dto.getDoctorId());

        // ✅ Ensure date is not null
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Date must not be null");
        }

        Disponibilite disponibilite = new Disponibilite();
        disponibilite.setDoctorId(dto.getDoctorId());
        disponibilite.setDate(dto.getDate()); // Should be correctly parsed from frontend as 'YYYY-MM-DD'
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
        if (dto.getDate() != null) existing.setDate(dto.getDate());

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
