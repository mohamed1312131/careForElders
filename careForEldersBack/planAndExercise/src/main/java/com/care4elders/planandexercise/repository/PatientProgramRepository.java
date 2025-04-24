package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.PatientProgram;
import com.care4elders.planandexercise.entity.ProgramStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientProgramRepository extends MongoRepository<PatientProgram, String> {
    Optional<PatientProgram> findByPatientIdAndProgramId(String patientId, String programId);
    List<PatientProgram> findByPatientIdAndStatus(String patientId, ProgramStatus status);

    List<PatientProgram> findByPatientId(String patientId);
}