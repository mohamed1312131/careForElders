package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.service.PaymentService;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PaymentSimulatorService;
import com.care4elders.patientbill.exception.BillNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments/simulator")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentSimulatorController {
    
    private final PaymentService paymentService;
    private final BillService billService;
    private final PaymentSimulatorService paymentSimulatorService;
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> showPaymentSimulator(@PathVariable String paymentId) {
        log.debug("Showing payment simulator for payment ID: {}", paymentId);
        
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // In a real application, this would return a view with a payment form
        // For simplicity, we're just returning payment details
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment simulator for online payment");
        response.put("payment", payment);
        response.put("instructions", "Use the POST endpoint to this same URL to simulate payment completion");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{paymentId}")
    public ResponseEntity<?> completePayment(
            @PathVariable String paymentId,
            @RequestParam(defaultValue = "true") boolean success) {
        
        log.info("Processing payment for payment ID: {}", paymentId);
        
        // Always simulate successful payment regardless of the success parameter
        boolean paymentSuccess = paymentSimulatorService.simulateOnlinePayment(paymentId);
        
        // Get the updated payment to include in the response
        Payment updatedPayment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Get the associated bill to include in the response
        Bill bill = null;
        try {
            bill = billService.getBillById(updatedPayment.getBillId());
        } catch (BillNotFoundException e) {
            log.warn("Could not find bill for payment: {}", e.getMessage());
        }
        
        // Create response with payment and bill details
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment processed successfully!");
        response.put("status", "COMPLETED");
        response.put("payment", updatedPayment);
        
        if (bill != null) {
            response.put("bill", bill);
            response.put("billStatus", bill.getStatus());
        }
        
        return ResponseEntity.ok(response);
    }
}
