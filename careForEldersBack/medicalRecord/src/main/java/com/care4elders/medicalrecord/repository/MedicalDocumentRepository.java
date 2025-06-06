package com.care4elders.medicalrecord.repository;

import com.care4elders.medicalrecord.entity.MedicalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalDocumentRepository extends MongoRepository<MedicalDocument, String> {
    List<MedicalDocument> findByUserId(String userId);
    Optional<MedicalDocument> findById(String id);

}
