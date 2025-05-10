package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.entity.Appointment;
import com.care4elders.medicalrecord.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repository;

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public Appointment save(Appointment appt) {
        return repository.save(appt);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
