// src/main/java/com/care4elders/patientbill/repository/BillRepository.java
package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillRepository extends MongoRepository<Bill, Long> {
    // Add these custom query methods
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByStatus(BillStatus status);
}