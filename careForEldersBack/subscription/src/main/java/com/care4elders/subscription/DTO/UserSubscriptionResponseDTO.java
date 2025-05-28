package com.care4elders.subscription.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionResponseDTO {
    private String planName;  // Changed from 'name' to be consistent
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private BigDecimal price;
    private Integer durationDays;
    private List<String> features;

    // Optionally keep these if needed
    private String userId;
    private String planId;
}