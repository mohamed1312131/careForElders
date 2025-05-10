package com.care4elders.paramedicalcare.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDTO {
    private String userId;
    private String serviceOfferingId;
    private String specialInstructions;
    private Integer requiredDurationHours;
}