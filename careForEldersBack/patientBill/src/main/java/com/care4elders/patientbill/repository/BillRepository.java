// src/main/java/com/care4elders/patientbill/repository/BillRepository.java
package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    // This inherits findAll() from JpaRepository
}