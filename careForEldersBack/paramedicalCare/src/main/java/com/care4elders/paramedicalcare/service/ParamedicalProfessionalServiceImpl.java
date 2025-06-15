package com.care4elders.paramedicalcare.service;

import com.care4elders.paramedicalcare.dto.*;
import com.care4elders.paramedicalcare.entity.Appointment;
import com.care4elders.paramedicalcare.entity.ParamedicalProfessional;
import com.care4elders.paramedicalcare.exception.EntityNotFoundException;
import com.care4elders.paramedicalcare.mapper.ProfessionalMapper;
import com.care4elders.paramedicalcare.repo.AppointmentRepository;
import com.care4elders.paramedicalcare.repo.ParamedicalProfessionalRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParamedicalProfessionalServiceImpl implements ParamedicalProfessionalService {

    private final ParamedicalProfessionalRepository professionalRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalMapper professionalMapper;
    private final GoogleMapsService googleMapsService;

    @Override
    public List<ParamedicalProfessionalDTO> getAllProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParamedicalProfessionalDTO getProfessionalById(String id) {
        return professionalRepository.findById(id)
                .map(professionalMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found with id: " + id));
    }

    @Override
    public List<ParamedicalProfessionalDTO> getProfessionalsBySpecialty(String specialty) {
        return professionalRepository.findBySpecialty(specialty)
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParamedicalProfessionalDTO createProfessional(CreateProfessionalRequest request) {
        ParamedicalProfessional professional = professionalMapper.toEntity(request);
        professional.setCreatedAt(LocalDateTime.now());
        professional.setUpdatedAt(LocalDateTime.now());

        // Geocode address if provided
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            try {
                LatLng coordinates = googleMapsService.geocodeAddress(request.getAddress());
                if (coordinates != null) {
                    professional.setLocation(new GeoJsonPoint(coordinates.lng, coordinates.lat));
                }
            } catch (ApiException | InterruptedException | IOException e) {
                log.warn("Failed to geocode address: {}", request.getAddress(), e);
            }
        }

        ParamedicalProfessional saved = professionalRepository.save(professional);
        return professionalMapper.toDTO(saved);
    }

    @Override
    public ParamedicalProfessionalDTO updateProfessional(String id, UpdateProfessionalRequest request) {
        ParamedicalProfessional existing = professionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found with id: " + id));

        professionalMapper.updateEntityFromRequest(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());

        ParamedicalProfessional updated = professionalRepository.save(existing);
        return professionalMapper.toDTO(updated);
    }

    @Override
    public void deleteProfessional(String id) {
        if (!professionalRepository.existsById(id)) {
            throw new EntityNotFoundException("Professional not found with id: " + id);
        }
        professionalRepository.deleteById(id);
    }

    @Override
    public List<ParamedicalProfessionalDTO> findNearbyProfessionals(String specialty, String elderLocation, int maxDistanceKm) {
        try {
            LatLng elderCoordinates = googleMapsService.geocodeAddress(elderLocation);
            List<ParamedicalProfessional> professionals;

            if (specialty != null && !specialty.isEmpty()) {
                professionals = professionalRepository.findBySpecialty(specialty);
            } else {
                professionals = professionalRepository.findAll();
            }

            return professionals.stream()
                    .map(prof -> {
                        ParamedicalProfessionalDTO dto = professionalMapper.toDTO(prof);
                        if (prof.getLocation() != null) {
                            LatLng profLocation = new LatLng(prof.getLocation().getY(), prof.getLocation().getX());
                            double distance = googleMapsService.calculateDistance(elderCoordinates, profLocation);
                            dto.setDistance(distance);
                        }
                        return dto;
                    })
                    .filter(dto -> dto.getDistance() == null || dto.getDistance() <= maxDistanceKm)
                    .sorted((a, b) -> Double.compare(
                            a.getDistance() != null ? a.getDistance() : Double.MAX_VALUE,
                            b.getDistance() != null ? b.getDistance() : Double.MAX_VALUE))
                    .collect(Collectors.toList());
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("Failed to geocode elder location: {}", elderLocation, e);
            return List.of();
        }
    }

    @Override
    public AppointmentDTO scheduleAppointment(AppointmentRequest request) {
        return bookAppointment(request);
    }

    @Override
    public List<AppointmentDTO> getElderAppointments(String elderId) {
        return appointmentRepository.findByElderId(elderId)
                .stream()
                .map(this::mapToAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParamedicalProfessionalDTO> findNearbyProfessionals(double latitude, double longitude, double radiusKm) {
        List<ParamedicalProfessional> allProfessionals = professionalRepository.findAll();
        LatLng searchLocation = new LatLng(latitude, longitude);

        return allProfessionals.stream()
                .map(prof -> {
                    ParamedicalProfessionalDTO dto = professionalMapper.toDTO(prof);
                    if (prof.getLocation() != null) {
                        LatLng profLocation = new LatLng(prof.getLocation().getY(), prof.getLocation().getX());
                        double distance = googleMapsService.calculateDistance(searchLocation, profLocation);
                        dto.setDistance(distance);
                    }
                    return dto;
                })
                .filter(dto -> dto.getDistance() == null || dto.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(
                        a.getDistance() != null ? a.getDistance() : Double.MAX_VALUE,
                        b.getDistance() != null ? b.getDistance() : Double.MAX_VALUE))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO bookAppointment(AppointmentRequest request) {
        // Verify professional exists
        ParamedicalProfessional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new EntityNotFoundException("Professional not found"));

        Appointment appointment = Appointment.builder()
                .elderId(request.getElderId())
                .professionalId(request.getProfessionalId())
                .appointmentTime(request.getAppointmentTime())
                .location(request.getLocation())
                .notes(request.getNotes())
                .status("SCHEDULED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        return AppointmentDTO.builder()
                .id(saved.getId())
                .elderId(saved.getElderId())
                .professionalId(saved.getProfessionalId())
                .professionalName(professional.getName())
                .specialty(professional.getSpecialty())
                .appointmentTime(saved.getAppointmentTime())
                .location(saved.getLocation())
                .notes(saved.getNotes())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByProfessional(String professionalId) {
        return appointmentRepository.findByProfessionalId(professionalId)
                .stream()
                .map(this::mapToAppointmentDTO)
                .collect(Collectors.toList());
    }

    private AppointmentDTO mapToAppointmentDTO(Appointment appointment) {
        Optional<ParamedicalProfessional> professional = professionalRepository.findById(appointment.getProfessionalId());

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .elderId(appointment.getElderId())
                .professionalId(appointment.getProfessionalId())
                .professionalName(professional.map(ParamedicalProfessional::getName).orElse("Unknown"))
                .specialty(professional.map(ParamedicalProfessional::getSpecialty).orElse("Unknown"))
                .appointmentTime(appointment.getAppointmentTime())
                .location(appointment.getLocation())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .build();
    }
}
