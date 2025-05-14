package com.care4elders.paramedicalcare.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "service_offerings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceOffering {
    @Id
    private String id;
    private String name; // Medical Service, Bathing and Grooming, etc.
    private String description;
    private BigDecimal pricePerHour;
    private String category; // Medical, Household, Companionship, etc.
    private boolean active = true;
    private String createdByDoctorId; // Reference to doctor from user service
    private LocalDateTime createdAt;
}