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
    
    @PostMapping
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest paymentRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in create payment request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        log.info("Creating new payment for bill ID: {}", paymentRequest.getBillId());
        Payment createdPayment = paymentService.createPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }
    
    private ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        List<com.care4elders.patientbill.exception.ValidationError> errors = bindingResult.getFieldErrors().stream()
            .map(error -> new com.care4elders.patientbill.exception.ValidationError(
                error.getField(), 
                error.getDefaultMessage()))
            .collect(java.util.stream.Collectors.toList());
            
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}
