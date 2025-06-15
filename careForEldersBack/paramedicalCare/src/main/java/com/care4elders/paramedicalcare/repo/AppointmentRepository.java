package com.care4elders.paramedicalcare.repo;

import com.care4elders.paramedicalcare.entity.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByProfessionalId(String professionalId);
    List<Appointment> findByElderId(String elderId);
}
