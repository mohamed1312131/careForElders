package com.care4elders.medicalrecord.repository;

import com.care4elders.medicalrecord.entity.MedicalNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalNoteRepository extends MongoRepository<MedicalNote, String> {
    List<MedicalNote> findByUserId(String userId);

}
