package com.care4elders.patientbill.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.care4elders.patientbill.dto.PaymentRequest;
import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.exception.PaymentProcessingException;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.model.PaymentMethod;
import com.care4elders.patientbill.repository.BillRepository;
import com.care4elders.patientbill.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public List<Payment> getAllPayments() {
        log.info("Getting all payments");
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(String id) {
        log.info("Getting payment with ID: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }
    
    public List<Payment> getPaymentsByBillId(String billId) {
        return paymentRepository.findByBillId(billId);
    }
    
    public Payment processPayment(PaymentRequest paymentRequest) {
        log.info("Processing payment for bill ID: {}", paymentRequest.getBillId());
        
        // Find the bill
        Bill bill = billRepository.findById(paymentRequest.getBillId())
                .orElseThrow(() -> new BillNotFoundException("Bill not found with ID: " + paymentRequest.getBillId()));
        
        // Create payment
        Payment payment = new Payment();
        payment.setBillId(paymentRequest.getBillId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentDate(new Date());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        
        // Process based on payment method
        if (paymentRequest.getPaymentMethod() == PaymentMethod.ONLINE) {
            processOnlinePayment(payment, paymentRequest);
        } else {
            processCashPayment(payment);
        }
        
        // Save payment
        payment = paymentRepository.save(payment);
        
        // Update bill
        updateBillAfterPayment(bill, payment);
        
        return payment;
    }
    
    private void processOnlinePayment(Payment payment, PaymentRequest paymentRequest) {
        log.info("Processing online payment");
        
        // Validate card details
        if (paymentRequest.getCardNumber() == null || paymentRequest.getCardholderName() == null ||
            paymentRequest.getExpiryMonth() == null || paymentRequest.getExpiryYear() == null ||
            paymentRequest.getCvv() == null) {
            throw new PaymentProcessingException("Card details are required for online payment");
        }
        
        // In a real application, you would integrate with a payment gateway here
        // For simulation, we'll just generate a transaction ID and mark as successful
        
        // Simple validation - card number should be 16 digits
        if (!paymentRequest.getCardNumber().replaceAll("\\s", "").matches("\\d{16}")) {
            payment.setSuccessful(false);
            payment.setPaymentDetails("Invalid card number");
            return;
        }
        
        // Generate transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setTransactionId(transactionId);
        payment.setSuccessful(true);
        payment.setPaymentDetails("Online payment processed successfully");
    }
    
    private void processCashPayment(Payment payment) {
        log.info("Processing cash payment");
        
        // For cash payments, we'll just mark it as successful
        payment.setSuccessful(true);
        payment.setPaymentDetails("Cash payment recorded");
    }
    
    private void updateBillAfterPayment(Bill bill, Payment payment) {
        if (payment.isSuccessful()) {
            // Update paid amount
            BigDecimal currentPaidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            bill.setPaidAmount(currentPaidAmount.add(payment.getAmount()));
            
            // Update balance
            bill.setBalanceAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()));
            
            // Update status
            if (bill.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                bill.setStatus("PAID");
            } else if (bill.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                bill.setStatus("PARTIALLY_PAID");
            }
            
            billRepository.save(bill);
        }
    }
}
