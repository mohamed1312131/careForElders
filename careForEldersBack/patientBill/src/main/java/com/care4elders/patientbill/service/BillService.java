// src/main/java/com/care4elders/patientbill/service/BillService.java
package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import java.util.List;

public interface BillService {
    List<Bill> getAllBills();
    Bill getBillById(String id);
    Bill createBill(Bill bill);
    Bill updateBill(String id, Bill bill);
    void deleteBill(String id);
}