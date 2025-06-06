package com.care4elders.subscription.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    @Id
    private String id;  // Auto-generated by MongoDB
    private String name;  // "Basic", "Standard", "Premium"
    private BigDecimal price;
    private Integer durationDays;  // e.g., 30 for monthly
    private List<String> features;

    public SubscriptionPlan(Object o, String basic, BigDecimal zero, int i, List<String> strings) {

    }
}
