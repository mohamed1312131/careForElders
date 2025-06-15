package com.care4elders.patientbill.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//import jakarta.validation.constraints.Positive;
import com.care4elders.patientbill.model.BillItem;

public class AutoBillRequest {
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    private String patientName; // Optional
    
    @Email(message = "Please provide a valid email address")
    private String patientEmail;
    
    @NotNull(message = "Service type is required")
    private BillItem.ServiceType serviceType;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;
    
    // Default constructor
    public AutoBillRequest() {}
    
    // Constructor with all fields
    public AutoBillRequest(String patientId, String patientName, String patientEmail, 
                          BillItem.ServiceType serviceType, BigDecimal totalAmount) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.serviceType = serviceType;
        this.totalAmount = totalAmount;
    }
    
    // Getters and setters
    public String getPatientId() { 
        return patientId; 
    }
    
    public void setPatientId(String patientId) { 
        this.patientId = patientId; 
    }
    
    public String getPatientName() { 
        return patientName; 
    }
    
    public void setPatientName(String patientName) { 
        this.patientName = patientName; 
    }
    
    public String getPatientEmail() { 
        return patientEmail; 
    }
    
    public void setPatientEmail(String patientEmail) { 
        this.patientEmail = patientEmail; 
    }
    
    public BillItem.ServiceType getServiceType() { 
        return serviceType; 
    }
    
    public void setServiceType(BillItem.ServiceType serviceType) { 
        this.serviceType = serviceType; 
    }
    
    public BigDecimal getTotalAmount() { 
        return totalAmount; 
    }
    
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount; 
    }
    
    @Override
    public String toString() {
        return "AutoBillRequest{" +
                "patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientEmail='" + patientEmail + '\'' +
                ", serviceType=" + serviceType +
                ", totalAmount=" + totalAmount +
                '}';
    }
}