package com.care4elders.subscription.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "user_subscriptions")
@Data

public class UserSubscription {
    @Id
    private String id;
    private String userId;  // Links to User in user-service
    private String planId;  // Links to SubscriptionPlan
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    // private String status;
}
