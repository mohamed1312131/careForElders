package com.care4elders.paramedicalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOfferingDTO {
    private String name;
    private String description;
    private BigDecimal pricePerHour;
    private String category;
    private String createdByDoctorId;
}
