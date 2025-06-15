package com.care4elders.medicalrecord.repository;

import com.care4elders.medicalrecord.entity.MedicalNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalNoteRepository extends MongoRepository<MedicalNote, String> {
    List<MedicalNote> findByUserId(String userId);
    Optional<MedicalNote> findById(String id);

}
