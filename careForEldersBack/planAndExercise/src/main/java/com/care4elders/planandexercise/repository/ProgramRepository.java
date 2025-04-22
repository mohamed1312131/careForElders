package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {}