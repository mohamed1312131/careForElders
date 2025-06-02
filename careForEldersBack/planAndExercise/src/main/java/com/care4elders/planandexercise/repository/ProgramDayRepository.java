package com.care4elders.planandexercise.repository;

import com.care4elders.planandexercise.entity.ProgramDay;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProgramDayRepository extends MongoRepository<ProgramDay, String> {
    List<ProgramDay> findByProgramId(String programId);
    Optional<ProgramDay> findByProgramIdAndDayNumber(String programId, int dayNumber);
    List<ProgramDay> findByProgramIdOrderByDayNumberAsc(String programId);
    Long countExercisesByProgramId(String programId);
    @Query(value = "{ 'programId' : ?0 }", count = true)
    Long countByProgramId(String programId);
    void deleteAllByIdIn(List<String> ids);

}
