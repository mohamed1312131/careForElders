package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.entity.PatientProgramAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientProgramAssignmentRepository extends MongoRepository<PatientProgramAssignment, String> {
    List<PatientProgramAssignment> findByPatientId(String patientId);
    Optional<PatientProgramAssignment> findByProgramIdAndPatientId(String programId, String patientId);
    long countByProgramId(String programId);
    List<PatientProgramAssignment> findByProgramId(String programId);
    void deleteByProgramId(String programId);
    @Query("SELECT COUNT(a) > 0 FROM PatientProgramAssignment a WHERE a.programId = :programId AND a.patientId = :patientId")
    boolean existsByProgramIdAndPatientId(@Param("programId") String programId,
                                          @Param("patientId") String patientId);

}
