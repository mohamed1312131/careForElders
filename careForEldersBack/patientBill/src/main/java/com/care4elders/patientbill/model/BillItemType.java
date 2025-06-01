package com.care4elders.patientbill.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum BillItemType {
    // Doctor care items
    APPOINTMENT("Appointment", 0),
    
    // Para-medical services
    BATCH_COOKING("Batch Cooking", 0),
    NURSE_VISITATION("Nurse Visitation", 0),
    CHECK_UP("Check-up", 0),
    
    // Subscription
    MONTHLY_SUBSCRIPTION("Monthly Subscription", 0);
    
    private final String displayName;
    private double defaultPrice;
    
    BillItemType(String displayName, double defaultPrice) {
        this.displayName = displayName;
        this.defaultPrice = defaultPrice;
    }
    
    // Getters
    public String getDisplayName() {
        return displayName;
    }
    
    public double getDefaultPrice() {
        return defaultPrice;
    }
    
    // Set default prices (could be loaded from config)
    public static void initializePrices(Map<BillItemType, Double> prices) {
        prices.forEach((type, price) -> type.defaultPrice = price);
    }
    
    // Get items by service type
    public static List<BillItemType> getItemsForServiceType(BillItem.ServiceType serviceType) {
        switch(serviceType) {
            case DOCTOR_CARE:
                return List.of(APPOINTMENT);
            case PARA_MEDICAL_SERVICES:
                return List.of(BATCH_COOKING, NURSE_VISITATION, CHECK_UP);
            case SUBSCRIPTION:
                return List.of(MONTHLY_SUBSCRIPTION);
            default:
                return Collections.emptyList();
        }
    }
}