package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    // Standard method for finding by patientId (Long)
    List<Bill> findByPatientId(Long patientId);
    
    // Use @Query annotation for custom queries when method name doesn't work
    @Query("{ 'patientId': ?0 }")
    List<Bill> findByPatientIdAsString(String patientIdStr);
}
