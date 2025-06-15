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
@RequestMapping("/api/payments")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final BillService billService;
    private final PaymentSimulatorService paymentSimulatorService;
    
    @PostMapping("/process/{paymentId}")
    public ResponseEntity<?> processPayment(
            @PathVariable String paymentId,
            @RequestParam String paymentMethod) {
        
        log.info("Processing payment ID: {} with method: {}", paymentId, paymentMethod);
        
        // Process the payment
        Payment payment = paymentService.processPayment(paymentId, paymentMethod);
        Map<String, Object> response = new HashMap<>();
        
        // Get the bill information
        Bill bill = null;
        try {
            bill = billService.getBillById(payment.getBillId());
            response.put("bill", bill);
            response.put("billStatus", bill.getStatus());
        } catch (BillNotFoundException e) {
            log.warn("Could not find bill for payment: {}", e.getMessage());
        }
        
        if ("CASH".equals(paymentMethod)) {
            response.put("message", "Payment completed successfully. Cash payment received.");
            response.put("payment", payment);
            return ResponseEntity.ok(response);
        } else if ("ONLINE".equals(paymentMethod)) {
            // For online payments, use the simulator
            boolean success = paymentSimulatorService.simulateOnlinePayment(paymentId);
            
            if (success) {
                // Get the updated payment after simulation
                Payment updatedPayment = paymentService.findById(paymentId)
                        .orElse(payment);
                
                // Try to get the updated bill
                try {
                    bill = billService.getBillById(payment.getBillId());
                    response.put("bill", bill);
                    response.put("billStatus", bill.getStatus());
                } catch (BillNotFoundException e) {
                    log.warn("Could not find bill for payment: {}", e.getMessage());
                }
                
                response.put("message", "Online payment processed successfully.");
                response.put("payment", updatedPayment);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Online payment failed. Please try again.");
                response.put("payment", payment);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("message", "Invalid payment method. Please choose CASH or ONLINE.");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method) {
        
        if (status != null && method != null) {
            return ResponseEntity.ok(paymentService.findByStatusAndMethod(status, method));
        } else if (status != null) {
            return ResponseEntity.ok(paymentService.findByStatus(status));
        } else if (method != null) {
            return ResponseEntity.ok(paymentService.findByMethod(method));
        } else {
            return ResponseEntity.ok(paymentService.findAll());
        }
    }
    
    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getPaymentsForBill(@PathVariable String billId) {
        return ResponseEntity.ok(paymentService.findByBillId(billId));
    }
}
