// src/main/java/com/care4elders/patientbill/service/BillService.java
package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillStatus;
import java.util.List;

public interface BillService {
    List<Bill> getAllBills();
    Bill getBillById(Long id);
    Bill createBill(Bill bill);
    Bill updateBill(Long id, Bill bill);
    void deleteBill(Long id);
    
    // Optional query methods
    List<Bill> getBillsByPatientId(Long patientId);
    List<Bill> getBillsByStatus(BillStatus status);
}