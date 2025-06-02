package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.entity.PatientProgramAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PatientProgramAssignmentRepository extends MongoRepository<PatientProgramAssignment, String> {
    List<PatientProgramAssignment> findByPatientId(String patientId);
    Optional<PatientProgramAssignment> findByProgramIdAndPatientId(String programId, String patientId);
    long countByProgramId(String programId);
    List<PatientProgramAssignment> findByProgramId(String programId);
    void deleteByProgramId(String programId);

}
