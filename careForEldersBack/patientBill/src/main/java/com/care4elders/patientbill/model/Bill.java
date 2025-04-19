// src/main/java/com/care4elders/patientbill/model/Bill.java
package com.care4elders.patientbill.model;

import java.time.LocalDate;
import java.util.List;

public class Bill {
    private Long id;
    private Long patientId;
    private String patientName;
    private LocalDate billDate;
    private LocalDate dueDate;
    private List<ServiceItem> services;
    private double totalAmount;
    private BillStatus status;
    private String notes;

    // Constructors
    public Bill() {}

    public Bill(Long id, Long patientId, String patientName, LocalDate billDate, LocalDate dueDate, 
                List<ServiceItem> services, double totalAmount, BillStatus status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.services = services;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<ServiceItem> getServices() {
        return services;
    }

    public void setServices(List<ServiceItem> services) {
        this.services = services;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}