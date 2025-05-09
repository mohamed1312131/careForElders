package com.care4elders.patientbill.repository;

import com.care4elders.patientbill.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    List<Payment> findByBillId(String billId);
    
    List<Payment> findByStatus(String status);
    
    List<Payment> findByPaymentMethod(String paymentMethod);
    
    List<Payment> findByStatusAndPaymentMethod(String status, String paymentMethod);
    
    List<Payment> findByBillIdAndStatus(String billId, String status);
}
