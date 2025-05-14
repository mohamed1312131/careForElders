package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.repository.PaymentRepository;
import com.care4elders.patientbill.exception.BillNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BillService billService;
    
    public Payment createPayment(String billId, double amount, String paymentMethod) {
        Payment payment = new Payment(billId, amount, paymentMethod);
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Payment processPayment(String paymentId, String paymentMethod) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setPaymentMethod(paymentMethod);
        
        if ("CASH".equals(paymentMethod)) {
            // For cash payments, mark as COMPLETED immediately
            payment.setStatus("COMPLETED");
            payment.setCompletedAt(LocalDateTime.now());
            
            // Save the payment first
            payment = paymentRepository.save(payment);
            
            // Update the bill status for cash payments
            try {
                // Fix: Don't call doubleValue() on a primitive double
                Bill updatedBill = billService.updateBillStatusAfterPayment(payment.getBillId(), payment.getAmount());
                log.info("Bill status updated to: {} after cash payment", updatedBill.getStatus());
            } catch (BillNotFoundException e) {
                log.error("Failed to update bill for cash payment: {}", e.getMessage());
                // We don't throw the exception here to ensure the payment is still processed
            }
        } else if ("ONLINE".equals(paymentMethod)) {
            // For online payments, set initial status to PENDING
            // Status will be updated by the payment simulator
            payment.setStatus("PENDING");
        }
        
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Payment completeOnlinePayment(String paymentId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (success) {
            payment.setStatus("PAID");
           // payment.setCompletedAt(LocalDateTime.now());
            
            // Save the payment first
            payment = paymentRepository.save(payment);
            
            // Update the bill status for successful online payments
            try {
                // Fix: Don't call doubleValue() on a primitive double
                Bill updatedBill = billService.updateBillStatusAfterPayment(payment.getBillId(), payment.getAmount());
                log.info("Bill status updated to: {} after online payment", updatedBill.getStatus());
            } catch (BillNotFoundException e) {
                log.error("Failed to update bill for online payment: {}", e.getMessage());
                // We don't throw the exception here to ensure the payment is still processed
            }
        } else {
            payment.setStatus("FAILED");
            payment = paymentRepository.save(payment);
        }
        
        return payment;
    }
    
    // Add these methods to provide controlled access to the repository
    
    public Optional<Payment> findById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }
    
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> findByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> findByMethod(String method) {
        return paymentRepository.findByPaymentMethod(method);
    }
    
    public List<Payment> findByStatusAndMethod(String status, String method) {
        return paymentRepository.findByStatusAndPaymentMethod(status, method);
    }
    
    public List<Payment> findByBillId(String billId) {
        return paymentRepository.findByBillId(billId);
    }
    
    public List<Payment> findByBillIdAndStatus(String billId, String status) {
        return paymentRepository.findByBillIdAndStatus(billId, status);
    }
}
