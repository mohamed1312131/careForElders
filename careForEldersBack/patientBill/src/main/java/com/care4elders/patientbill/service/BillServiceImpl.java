package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;

import com.care4elders.patientbill.repository.BillRepository;
import com.care4elders.patientbill.exception.BillNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    public List<Bill> getBillsByPatientId(String patientId) {
        log.debug("Fetching bills for patient id: {}", patientId);
        return billRepository.findByPatientId(patientId);
    }
    
    @Override
    public List<Bill> getBillsByPatientIdString(String patientIdStr) {
        log.debug("Fetching bills for patient id string: {}", patientIdStr);
        return billRepository.findByPatientId(patientIdStr);
    }
    
    @Override
    public List<Bill> getBillsByServiceType(BillItem.ServiceType serviceType) {
        log.debug("Fetching bills for service type: {}", serviceType);
        return billRepository.findAll().stream()
            .filter(bill -> bill.getServiceType() == serviceType || 
                   (bill.getItems() != null && bill.getItems().stream()
                    .anyMatch(item -> item.getServiceType() == serviceType)))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Bill> getBillsByStatus(String status) {
        log.debug("Fetching bills with status: {}", status);
        return billRepository.findByStatus(status);
    }
    

    @Override
    @Transactional
    public Bill addBillItem(String billId, BillItem billItem) throws BillNotFoundException {
        log.debug("Adding item to bill with id: {}", billId);
        
        Bill bill = getBillById(billId);
        
        // Set bill reference and timestamps
        billItem.setBillId(billId);
        billItem.setCreatedAt(LocalDateTime.now());
        billItem.setUpdatedAt(LocalDateTime.now());
        billItem.calculateAmount();
        
        // Add item to bill
        if (bill.getItems() == null) {
            bill.setItems(new ArrayList<>());
        }
        bill.getItems().add(billItem);
        
        // Recalculate totals and update status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
        return billRepository.save(bill);
    }
    
    @Override
    @Transactional
    public Bill updateBillItem(String billId, String itemId, BillItem updatedItem) throws BillNotFoundException {
        log.debug("Updating item {} in bill {}", itemId, billId);
        
        Bill bill = getBillById(billId);
        
        // Find and update the item
        Optional<BillItem> existingItem = bill.getItems().stream()
            .filter(item -> itemId.equals(item.getId()))
            .findFirst();
            
        if (existingItem.isEmpty()) {
            throw new BillNotFoundException("Bill item not found with id: " + itemId);
        }
        
        // Update the item
        BillItem itemToUpdate = existingItem.get();
        itemToUpdate.setServiceType(updatedItem.getServiceType());
        itemToUpdate.setDescription(updatedItem.getDescription());
        itemToUpdate.setQuantity(updatedItem.getQuantity());
        itemToUpdate.setUnitPrice(updatedItem.getUnitPrice());
        itemToUpdate.setUnit(updatedItem.getUnit());
        itemToUpdate.setServiceDate(updatedItem.getServiceDate());
        itemToUpdate.setUpdatedAt(LocalDateTime.now());
        itemToUpdate.calculateAmount();
        
        // Recalculate totals and update status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
        return billRepository.save(bill);
    }
    
    @Override
    @Transactional
    public Bill removeBillItem(String billId, String itemId) throws BillNotFoundException {
        log.debug("Removing item {} from bill {}", itemId, billId);
        
        Bill bill = getBillById(billId);
        
        // Remove the item
        boolean removed = bill.getItems().removeIf(item -> itemId.equals(item.getId()));
        
        if (!removed) {
            throw new BillNotFoundException("Bill item not found with id: " + itemId);
        }
        
        // Recalculate totals and update status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
        return billRepository.save(bill);
    }
    
    @Override
    @Transactional
    public Bill recordPayment(String billId, BigDecimal paymentAmount, String paymentMethod) throws BillNotFoundException {
        log.debug("Recording payment of {} for bill id: {} using method: {}", paymentAmount, billId, paymentMethod);
        
        Bill bill = getBillById(billId);
        
        // Validate payment amount
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        
        BigDecimal remainingBalance = bill.getBalanceAmount() != null ? bill.getBalanceAmount() : bill.getTotalAmount();
        if (paymentAmount.compareTo(remainingBalance) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed remaining balance");
        }
        
        // Update paid amount
        BigDecimal currentPaidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaidAmount.add(paymentAmount);
        bill.setPaidAmount(newPaidAmount);
        
        // Recalculate balance and update status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
        return billRepository.save(bill);
    }
    
    @Override
    public void recalculateBillTotals(Bill bill) {
        if (bill.getItems() != null && !bill.getItems().isEmpty()) {
            BigDecimal totalAmount = bill.getItems().stream()
                .map(BillItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            bill.setTotalAmount(totalAmount);
            
            BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            bill.setBalanceAmount(totalAmount.subtract(paidAmount));
        } else {
            bill.setTotalAmount(BigDecimal.ZERO);
            bill.setBalanceAmount(BigDecimal.ZERO);
        }
    }
    
    @Override
    public void updateBillStatus(Bill bill) {
        BigDecimal totalAmount = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setStatus("DRAFT");
        } else if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setStatus("PENDING");
        } else if (paidAmount.compareTo(totalAmount) >= 0) {
            bill.setStatus("PAID");
        } else {
            bill.setStatus("PARTIALLY_PAID");
        }
    }
    
    
    @Override
    public Bill createBill(Bill bill) {
        log.debug("Creating new bill for patient: {}", bill.getPatientName());
        bill.setBillNumber(generateRandomBillNumber());
        
        // Set default dates if not provided
        if (bill.getBillDate() == null) {
            bill.setBillDate(new java.util.Date());
        }
        if (bill.getDueDate() == null) {
            bill.setDueDate(new java.util.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        }
        
        // Process bill items
        if (bill.getItems() != null) {
            bill.getItems().forEach(item -> {
                item.setBillId(bill.getId());
                if (item.getCreatedAt() == null) {
                    item.setCreatedAt(LocalDateTime.now());
                }
                item.setUpdatedAt(LocalDateTime.now());
                item.calculateAmount();
            });
        }
        
        // Calculate totals and set status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
        return billRepository.save(bill);
    }
    
    private String generateRandomBillNumber() {
        int length = 8;
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return "BILL-" + sb.toString();
    }
    
    @Override
    public Bill updateBill(String id, Bill bill) throws BillNotFoundException {
        log.debug("Updating bill with id: {}", id);
        if (!billRepository.existsById(id)) {
            log.error("Bill not found with id: {}", id);
            throw new BillNotFoundException("Bill not found with id: " + id);
        }
        
        bill.setId(id);
        
        // Process bill items
        if (bill.getItems() != null) {
            bill.getItems().forEach(item -> {
                item.setBillId(id);
                item.setUpdatedAt(LocalDateTime.now());
                if (item.getCreatedAt() == null) {
                    item.setCreatedAt(LocalDateTime.now());
                }
                item.calculateAmount();
            });
        }
        
        // Calculate totals and update status
        recalculateBillTotals(bill);
        updateBillStatus(bill);
        
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
    
    @Override
    @Transactional
    public Bill updateBillStatusAfterPayment(String billId, double paymentAmount) throws BillNotFoundException {
        return recordPayment(billId, BigDecimal.valueOf(paymentAmount), "CASH");
    }
}