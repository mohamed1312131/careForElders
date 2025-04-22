package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.PatientProgram;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientProgramRepository extends MongoRepository<PatientProgram, String> {}