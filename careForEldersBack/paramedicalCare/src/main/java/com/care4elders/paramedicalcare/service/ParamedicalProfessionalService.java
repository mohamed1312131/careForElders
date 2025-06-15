package com.care4elders.paramedicalcare.service;

import com.care4elders.paramedicalcare.dto.*;

import java.util.List;

public interface ParamedicalProfessionalService {
    ParamedicalProfessionalDTO createProfessional(CreateProfessionalRequest request);
    List<ParamedicalProfessionalDTO> getAllProfessionals();
    ParamedicalProfessionalDTO getProfessionalById(String id);
    List<ParamedicalProfessionalDTO> getProfessionalsBySpecialty(String specialty);
    ParamedicalProfessionalDTO updateProfessional(String id, UpdateProfessionalRequest request); // For PUT/PATCH
    void deleteProfessional(String id);

    // Location-based methods
    List<ParamedicalProfessionalDTO> findNearbyProfessionals(String specialty, String elderLocation, int maxDistanceKm);

    // Appointment methods
    AppointmentDTO scheduleAppointment(AppointmentRequest request);
    List<AppointmentDTO> getElderAppointments(String elderId);

    List<ParamedicalProfessionalDTO> findNearbyProfessionals(double latitude, double longitude, double radiusKm);

    AppointmentDTO bookAppointment(AppointmentRequest request);

    List<AppointmentDTO> getAppointmentsByProfessional(String professionalId);
}