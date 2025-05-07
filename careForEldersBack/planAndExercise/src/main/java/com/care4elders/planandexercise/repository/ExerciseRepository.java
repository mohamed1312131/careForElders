package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {
    @Override
    List<Exercise> findAllById(Iterable<String> ids);
    List<Exercise> findAllByIdIn(List<String> ids);
}
