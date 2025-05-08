package com.care4elders.patientbill.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.care4elders.patientbill.model.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "Bill ID is required")
    private String billId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private Date paymentDate;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private String transactionId;
    
    private String paymentDetails;
}
