package com.care4elders.patientbill.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.DBRef;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    private String id;
    
    @NotNull(message = "Bill ID is required")
    private String billId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment date is required")
    private Date paymentDate;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private String transactionId;
    
    private String paymentDetails;
    
    private boolean successful;
}
