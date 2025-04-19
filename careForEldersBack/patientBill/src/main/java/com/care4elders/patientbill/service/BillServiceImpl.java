// src/main/java/com/care4elders/patientbill/service/BillServiceImpl.java
package com.care4elders.patientbill.service;

import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillStatus;
import com.care4elders.patientbill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final SequenceGeneratorService sequenceGenerator;

    @Autowired
    public BillServiceImpl(BillRepository billRepository, 
                         SequenceGeneratorService sequenceGenerator) {
        this.billRepository = billRepository;
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + id));
    }

    @Override
    public Bill createBill(Bill bill) {
        bill.setId(sequenceGenerator.generateSequence(Bill.SEQUENCE_NAME));
        return billRepository.save(bill);
    }

    @Override
    public Bill updateBill(Long id, Bill billDetails) {
        Bill bill = getBillById(id);
        
        bill.setPatientId(billDetails.getPatientId());
        bill.setPatientName(billDetails.getPatientName());
        bill.setBillDate(billDetails.getBillDate());
        bill.setDueDate(billDetails.getDueDate());
        bill.setServices(billDetails.getServices());
        bill.setTotalAmount(billDetails.getTotalAmount());
        bill.setStatus(billDetails.getStatus());
        bill.setNotes(billDetails.getNotes());
        
        return billRepository.save(bill);
    }

    @Override
    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        billRepository.delete(bill);
    }

    @Override
    public List<Bill> getBillsByPatientId(Long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    @Override
    public List<Bill> getBillsByStatus(BillStatus status) {
        return billRepository.findByStatus(status);
    }
}