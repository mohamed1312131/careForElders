package com.care4elders.patientbill.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    
    @Id
    private String id;
    
    private String billId;
    private double amount; // This is a primitive double, not a BigDecimal
    private String paymentMethod; // CASH, ONLINE
    private String status; // PENDING, COMPLETED, FAILED
    private LocalDateTime timestamp;
    private LocalDateTime completedAt;
    
    public Payment(String billId, double amount, String paymentMethod) {
        this.id = UUID.randomUUID().toString();
        this.billId = billId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }
    
    // Getter for amount returns primitive double
    public double getAmount() {
        return amount;
    }
    
    // Setter for completedAt
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
