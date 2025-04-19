// src/main/java/com/care4elders/patientbill/model/ServiceItem.java
package com.care4elders.patientbill.model;

public class ServiceItem {
    private String serviceName;
    private String description;
    private double rate;
    private int quantity;
    
    // Constructors
    public ServiceItem() {}

    public ServiceItem(String serviceName, String description, double rate, int quantity) {
        this.serviceName = serviceName;
        this.description = description;
        this.rate = rate;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}