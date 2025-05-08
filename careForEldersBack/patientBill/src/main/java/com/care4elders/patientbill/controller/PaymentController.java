package com.care4elders.patientbill.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care4elders.patientbill.dto.PaymentRequest;
import com.care4elders.patientbill.exception.ValidationErrorResponse;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<Payment>> getPaymentsByBillId(@PathVariable String billId) {
        log.info("Fetching payments for bill ID: {}", billId);
        List<Payment> payments = paymentService.getPaymentsByBillId(billId);
        return ResponseEntity.ok(payments);
    }
    @GetMapping
public ResponseEntity<List<Payment>> getAllPayments() {
    log.info("Fetching all payments");
    List<Payment> payments = paymentService.getAllPayments();
    return ResponseEntity.ok(payments);
}
//@GetMapping("/{id}")
//public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
    //log.info("Fetching payment with ID: {}", id);
    //Payment payment = paymentService.getPaymentById(id);
    //return ResponseEntity.ok(payment);
//}
 
}
