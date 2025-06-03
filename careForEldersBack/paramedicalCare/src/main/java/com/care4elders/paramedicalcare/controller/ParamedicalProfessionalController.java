package com.care4elders.paramedicalcare.controller;

import com.care4elders.paramedicalcare.dto.*;
import com.care4elders.paramedicalcare.service.ParamedicalProfessionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paramedical")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequiredArgsConstructor
@Validated
public class ParamedicalProfessionalController {

    private final ParamedicalProfessionalService service;

    @PostMapping
    public ResponseEntity<ParamedicalProfessionalDTO> createProfessional(
            @Valid @RequestBody CreateProfessionalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createProfessional(request));
    }

    @GetMapping
    public ResponseEntity<List<ParamedicalProfessionalDTO>> getAllProfessionals() {
        return ResponseEntity.ok(service.getAllProfessionals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParamedicalProfessionalDTO> getProfessionalById(
            @PathVariable String id) {
        return ResponseEntity.ok(service.getProfessionalById(id));
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<ParamedicalProfessionalDTO>> getBySpecialty(
            @PathVariable String specialty) {
        return ResponseEntity.ok(service.getProfessionalsBySpecialty(specialty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParamedicalProfessionalDTO> updateProfessional(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfessionalRequest request) {
        return ResponseEntity.ok(service.updateProfessional(id, request));
    }

    @PatchMapping("/{id}")  // NEW: Partial update endpoint
    public ResponseEntity<ParamedicalProfessionalDTO> partialUpdateProfessional(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfessionalRequest request) {
        return ResponseEntity.ok(service.updateProfessional(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable String id) {
        service.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
    // New endpoint: Find nearby professionals
    @GetMapping("/nearby")
    public ResponseEntity<List<ParamedicalProfessionalDTO>> findNearbyProfessionals(
            @RequestParam String location,
            @RequestParam(required = false) String specialty,
            @RequestParam(defaultValue = "10") int distanceKm) {
        return ResponseEntity.ok(service.findNearbyProfessionals(specialty, location, distanceKm));
    }

    // New endpoint: Schedule appointment
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> scheduleAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.scheduleAppointment(request));
    }

    // New endpoint: Get elder's appointments
    @GetMapping("/elders/{elderId}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getElderAppointments(
            @PathVariable String elderId) {
        return ResponseEntity.ok(service.getElderAppointments(elderId));
    }
}
