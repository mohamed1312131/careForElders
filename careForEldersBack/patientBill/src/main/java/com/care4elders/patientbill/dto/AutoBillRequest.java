package com.care4elders.patientbill.dto;

import java.math.BigDecimal;

import com.care4elders.patientbill.model.BillItem;

public class AutoBillRequest {
    private String patientId;
    private String patientName; // Optional
    private BillItem.ServiceType serviceType;
    private BigDecimal totalAmount;
    
    // Getters and setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public BillItem.ServiceType getServiceType() { return serviceType; }
    public void setServiceType(BillItem.ServiceType serviceType) { this.serviceType = serviceType; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
