package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    // Add the correct method signature for findByPatientId
    List<Bill> findByPatientId(Long patientId);
    
    // If you need to search by patient ID as a string, add this method
    // This assumes patientId is stored as a String in some cases
    List<Bill> findByPatientIdString(String patientIdString);
}
