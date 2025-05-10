package com.care4elders.patientbill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String message;
    private String paymentId;
    private String status;
    private String redirectUrl; // For online payments
}
