package com.care4elders.patientbill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardRequest {
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    private String cardNumber;
    
    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;
    
    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in format MM/YY")
    private String expiryDate;
    
    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;
    
    @NotBlank(message = "Payment ID is required")
    private String paymentId;
    
    @NotBlank(message = "Bill ID is required")
    private String billId;

    /**
     * Gets the bill identifier, which could be either the MongoDB ID or the billNumber
     * @return The bill identifier
     */
    public String getBillIdentifier() {
        return this.billId;
    }
}
