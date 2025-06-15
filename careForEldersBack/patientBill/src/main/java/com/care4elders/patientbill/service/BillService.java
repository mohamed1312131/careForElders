package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;

import com.care4elders.patientbill.exception.BillNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface BillService {
    // Basic CRUD operations
    List<Bill> getAllBills();
    Bill getBillById(String id) throws BillNotFoundException;
    Bill createBill(Bill bill);
    Bill updateBill(String id, Bill bill) throws BillNotFoundException;
    void deleteBill(String id) throws BillNotFoundException;
    
    // Query methods
    List<Bill> getBillsByPatientId(String patientId);
    List<Bill> getBillsByPatientIdString(String patientIdStr);
    List<Bill> getBillsByServiceType(BillItem.ServiceType serviceType);
    List<Bill> getBillsByStatus(String status);
    // Bill item management
    Bill addBillItem(String billId, BillItem billItem) throws BillNotFoundException;
    Bill updateBillItem(String billId, String itemId, BillItem billItem) throws BillNotFoundException;
    Bill removeBillItem(String billId, String itemId) throws BillNotFoundException;
    
    // Payment processing
    Bill recordPayment(String billId, BigDecimal paymentAmount, String paymentMethod) throws BillNotFoundException;
    Bill updateBillStatusAfterPayment(String billId, double paymentAmount) throws BillNotFoundException;
    
    // Utility methods
    void recalculateBillTotals(Bill bill);
    void updateBillStatus(Bill bill);
}