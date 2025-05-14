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
    
    /**
     * Updates the bill status after a payment has been processed.
     * This method calculates the new balance, updates the paid amount,
     * and sets the appropriate status based on the payment.
     *
     * @param billId The ID of the bill to update
     * @param paymentAmount The amount of the payment that was processed
     * @return The updated Bill object
     * @throws BillNotFoundException if the bill with the given ID is not found
     */
    Bill updateBillStatusAfterPayment(String billId, double paymentAmount) throws BillNotFoundException;
}
