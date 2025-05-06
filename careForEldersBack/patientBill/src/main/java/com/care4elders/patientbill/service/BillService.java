package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.exception.BillNotFoundException;

import java.util.List;

public interface BillService {
    List<Bill> getAllBills();
    Bill getBillById(String id) throws BillNotFoundException;
    List<Bill> getBillsByPatientId(Long patientId);
    List<Bill> getBillsByPatientIdString(String patientIdStr);
    Bill createBill(Bill bill);
    Bill updateBill(String id, Bill bill) throws BillNotFoundException;
    void deleteBill(String id) throws BillNotFoundException;
}
