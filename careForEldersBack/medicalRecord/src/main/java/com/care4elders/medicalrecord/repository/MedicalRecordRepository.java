package com.care4elders.medicalrecord.repository;

import com.care4elders.medicalrecord.entity.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.management.Query;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    Optional<MedicalRecord> findByUserId(String userId);


}
