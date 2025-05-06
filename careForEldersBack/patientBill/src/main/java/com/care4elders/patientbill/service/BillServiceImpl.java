package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.repository.BillRepository;
//import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.exception.BillNotFoundException;

import org.springframework.stereotype.Service;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    
    private final BillRepository billRepository;
    
    @Override
    public List<Bill> getAllBills() {
        log.debug("Fetching all bills from database");
        return billRepository.findAll();
    }
    
    @Override
    public Bill getBillById(String id) throws BillNotFoundException {
        log.debug("Fetching bill with id: {}", id);
        return billRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Bill not found with id: {}", id);
                return new BillNotFoundException("Bill not found with id: " + id);
            });
    }
    
    @Override
    public List<Bill> getBillsByPatientId(Long patientId) {
        log.debug("Fetching bills for patient id: {}", patientId);
        return billRepository.findByPatientId(patientId);
    }
    
    @Override
    public List<Bill> getBillsByPatientIdString(String patientIdStr) {
        log.debug("Fetching bills for patient id string: {}", patientIdStr);
        try {
            // Try to convert to Long and use the standard method
            Long patientId = Long.parseLong(patientIdStr);
            return billRepository.findByPatientId(patientId);
        } catch (NumberFormatException e) {
            log.warn("Could not parse patient ID '{}' as Long, using string query", patientIdStr);
            // If conversion fails, use the custom query method
            return billRepository.findByPatientIdAsString(patientIdStr);
        }
    }
    
    @Override
    public Bill createBill(Bill bill) {
        log.debug("Creating new bill for patient: {}", bill.getPatientName());
        return billRepository.save(bill);
    }
    
    @Override
    public Bill updateBill(String id, Bill bill) throws BillNotFoundException {
        log.debug("Updating bill with id: {}", id);
        if (!billRepository.existsById(id)) {
            log.error("Bill not found with id: {}", id);
            throw new BillNotFoundException("Bill not found with id: " + id);
        }
        bill.setId(id);
        return billRepository.save(bill);
    }
    
    @Override
    public void deleteBill(String id) throws BillNotFoundException {
        log.debug("Deleting bill with id: {}", id);
        if (!billRepository.existsById(id)) {
            log.error("Bill not found with id: {}", id);
            throw new BillNotFoundException("Bill not found with id: " + id);
        }
        billRepository.deleteById(id);
    }
}
