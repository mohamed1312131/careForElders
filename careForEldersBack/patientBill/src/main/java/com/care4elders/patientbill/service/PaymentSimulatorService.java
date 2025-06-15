package com.care4elders.patientbill.service;

//import com.care4elders.patientbill.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSimulatorService {
    
    private final PaymentService paymentService;
    
    @Transactional
    public boolean simulateOnlinePayment(String paymentId) {
        log.info("Simulating online payment for payment ID: {}", paymentId);
        
        // Simulate payment processing delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Payment simulation interrupted", e);
        }
        
        // Always succeed
        boolean success = true;
        
        try {
            // Update payment status - this will also update the bill status
            paymentService.completeOnlinePayment(paymentId, success);
            log.info("Payment simulation completed successfully for payment ID: {}", paymentId);
            return success;
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            return false;
        }
    }
}
