package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.ExerciseCompletion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExerciseCompletionRepository extends MongoRepository<ExerciseCompletion, String> {
    long countByPatientProgramId(String patientProgramId);
    List<ExerciseCompletion> findByPatientProgramId(String patientProgramId);

}