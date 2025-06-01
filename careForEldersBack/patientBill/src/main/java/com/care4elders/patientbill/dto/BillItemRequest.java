package com.care4elders.patientbill.dto;

import com.care4elders.patientbill.model.BillItemType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BillItemRequest {
    @NotNull
    private BillItemType type;
    
    @Min(1)
    private int quantity = 1;
    
    @Positive
    private double unitPrice;
}