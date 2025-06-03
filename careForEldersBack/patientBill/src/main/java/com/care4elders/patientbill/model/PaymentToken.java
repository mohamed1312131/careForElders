package com.care4elders.patientbill.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "payment_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentToken {
    
    @Id
    private String id;
    
    private String token;
    private String billId;
    private String patientId;
    private String patientEmail;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime usedAt;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !used && !isExpired();
    }
}