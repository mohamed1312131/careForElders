package com.care4elders.patientbill.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care4elders.patientbill.dto.CreditCardRequest;
import com.care4elders.patientbill.dto.PaymentResponse;
import com.care4elders.patientbill.exception.ValidationError;
import com.care4elders.patientbill.exception.ValidationErrorResponse;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment-simulation")
@RequiredArgsConstructor
@Slf4j
public class PaymentSimulationController {
    
    private final PaymentService paymentService;
    private final BillService billService;
    
    @PostMapping("/process-credit-card")
    public ResponseEntity<?> processCreditCard(@Valid @RequestBody CreditCardRequest creditCardRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in credit card request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        log.info("Processing credit card payment for payment ID: {}", creditCardRequest.getPaymentId());
        
        // Simulate credit card processing
        boolean paymentSuccessful = validateAndProcessCreditCard(creditCardRequest);
        
        if (paymentSuccessful) {
            try {
                // Generate a transaction ID
                String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                // Update payment status
                Payment payment = paymentService.getPaymentById(creditCardRequest.getPaymentId());
                payment.setSuccessful(true);
                payment.setTransactionId(transactionId);
                payment.setPaymentDetails("Credit card payment processed successfully");
                paymentService.updatePayment(payment);
                
                // Update bill status to PAID
                Bill bill = billService.getBillById(creditCardRequest.getBillId());
                bill.setStatus("PAID");
                bill.setPaidAmount(bill.getTotalAmount());
                bill.setBalanceAmount(java.math.BigDecimal.ZERO);
                billService.updateBill(bill.getId(), bill);
                
                // Return success response
                PaymentResponse response = new PaymentResponse(
                    creditCardRequest.getPaymentId(),
                    creditCardRequest.getBillId(),
                    "SUCCESS",
                    "Payment processed successfully",
                    transactionId
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error updating payment/bill status", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse(
                        creditCardRequest.getPaymentId(),
                        creditCardRequest.getBillId(),
                        "ERROR",
                        "Error updating payment status: " + e.getMessage(),
                        null
                    ));
            }
        } else {
            return ResponseEntity.badRequest()
                .body(new PaymentResponse(
                    creditCardRequest.getPaymentId(),
                    creditCardRequest.getBillId(),
                    "FAILED",
                    "Credit card validation failed",
                    null
                ));
        }
    }
    
    private boolean validateAndProcessCreditCard(CreditCardRequest creditCardRequest) {
        // This is a simulation, so we'll do some basic validation
        
        // Check if card number is valid (Luhn algorithm check)
        if (!isValidCreditCardNumber(creditCardRequest.getCardNumber())) {
            log.warn("Invalid credit card number");
            return false;
        }
        
        // Check if expiry date is valid (not expired)
        if (!isValidExpiryDate(creditCardRequest.getExpiryDate())) {
            log.warn("Expired credit card");
            return false;
        }
        
        // In a real system, you would integrate with a payment gateway
        // For this simulation, we'll just return true (success)
        log.info("Credit card validation successful");
        return true;
    }
    
    private boolean isValidCreditCardNumber(String cardNumber) {
        // Simple Luhn algorithm implementation
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
    
    private boolean isValidExpiryDate(String expiryDate) {
        // Parse MM/YY format
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to 20YY
        
        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        
        // Check if card is expired
        if (year < currentYear) {
            return false;
        }
        if (year == currentYear && month < currentMonth) {
            return false;
        }
        
        return true;
    }
    
    private ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        List<ValidationError> errors = bindingResult.getFieldErrors().stream()
            .map(error -> new ValidationError(
                error.getField(), 
                error.getDefaultMessage()))
            .collect(java.util.stream.Collectors.toList());
            
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}
