package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.Program;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProgramRepository extends MongoRepository<Program, String> {
    List<Program> findByDoctorId(String doctorId);
    @Query(value = "{ 'programId' : ?0 }", count = true)
    Long countDaysByProgramId(String programId);

    @Aggregation(pipeline = {
            "{ $match: { 'programId' : ?0 } }",
            "{ $unwind: '$exerciseIds' }",
            "{ $count: 'totalExercises' }"
    })
    Long countExercisesByProgramId(String programId);
}
