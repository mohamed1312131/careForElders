package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.dto.PaymentRequest;
import com.care4elders.patientbill.dto.PaymentResponse;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.service.PaymentService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BillPaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/{billId}/payments")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable String billId,
            @Valid @RequestBody PaymentRequest paymentRequest) {
        
        // Create a new payment for the bill
        Payment payment = paymentService.createPayment(
                billId, 
                paymentRequest.getAmount(), 
                paymentRequest.getPaymentMethod()
        );
        
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setStatus(payment.getStatus());
        
        if ("CASH".equals(payment.getPaymentMethod())) {
            // For cash payments, update status immediately
            payment = paymentService.completeOnlinePayment(payment.getId(), true);
            
            response.setMessage("Cash payment recorded successfully. Please collect cash from patient.");
        } else if ("ONLINE".equals(payment.getPaymentMethod())) {
            // For online payments, provide a URL to the payment simulator
            response.setMessage("Online payment initiated. Redirecting to payment gateway...");
            response.setRedirectUrl("/api/payments/simulator/" + payment.getId());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{billId}/payments")
    public ResponseEntity<List<Payment>> getBillPayments(@PathVariable String billId) {
        List<Payment> payments = paymentService.findByBillId(billId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/{billId}/payments/status/{status}")
    public ResponseEntity<List<Payment>> getBillPaymentsByStatus(
            @PathVariable String billId,
            @PathVariable String status) {
        List<Payment> payments = paymentService.findByBillIdAndStatus(billId, status);
        return ResponseEntity.ok(payments);
    }
}
