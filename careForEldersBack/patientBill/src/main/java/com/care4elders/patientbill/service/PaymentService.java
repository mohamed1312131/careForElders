package com.care4elders.patientbill.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.care4elders.patientbill.dto.PaymentRequest;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByBillId(String billId) {
        return paymentRepository.findByBillId(billId);
    }
    
    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }
    
    public Payment createPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setBillId(paymentRequest.getBillId());
        payment.setAmount(paymentRequest.getAmount());
        
        // Set payment date to current date if not provided
        payment.setPaymentDate(paymentRequest.getPaymentDate() != null ? 
            paymentRequest.getPaymentDate() : new Date());
        
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentDetails(paymentRequest.getPaymentDetails());
        
        // Set as successful if it's not a PENDING or ONLINE payment
        payment.setSuccessful(paymentRequest.getPaymentMethod() != com.care4elders.patientbill.model.PaymentMethod.PENDING &&
                             paymentRequest.getPaymentMethod() != com.care4elders.patientbill.model.PaymentMethod.ONLINE);
        
        log.info("Creating payment for bill ID: {}", paymentRequest.getBillId());
        return paymentRepository.save(payment);
    }
    
    public Payment updatePayment(Payment payment) {
        log.info("Updating payment with ID: {}", payment.getId());
        return paymentRepository.save(payment);
    }
}
