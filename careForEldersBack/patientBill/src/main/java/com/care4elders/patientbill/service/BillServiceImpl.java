// src/main/java/com/care4elders/patientbill/service/BillServiceImpl.java
package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));
    }

    @Override
    public Bill createBill(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public Bill updateBill(Long id, Bill bill) {
        Bill existingBill = getBillById(id);
        // Update fields from bill to existingBill
        // ...
        return billRepository.save(existingBill);
    }

    @Override
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
}