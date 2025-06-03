package com.care4elders.patientbill.dto;

import com.care4elders.patientbill.model.Bill;

public  class AutoBillResponse {
    private Bill bill;
    private boolean emailSent;
    private String emailAddress;
    
    public AutoBillResponse(Bill bill, boolean emailSent, String emailAddress) {
        this.bill = bill;
        this.emailSent = emailSent;
        this.emailAddress = emailAddress;
    }
    
    // Getters and setters
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
}
