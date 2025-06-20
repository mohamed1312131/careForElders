package com.care4elders.patientbill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "Bill ID is required")
    private String billId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private double amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // "CASH" or "ONLINE"
}
