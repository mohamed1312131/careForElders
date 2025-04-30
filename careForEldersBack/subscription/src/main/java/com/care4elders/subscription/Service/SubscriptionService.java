package com.care4elders.subscription.Service;

import com.care4elders.subscription.entity.SubscriptionPlan;
import com.care4elders.subscription.entity.UserSubscription;
import com.care4elders.subscription.repository.SubscriptionPlanRepo;
import com.care4elders.subscription.repository.UserSubscriptionRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@Service

public class SubscriptionService {
    private final SubscriptionPlanRepo planRepo;
    private final UserSubscriptionRepo userSubRepo;

    public SubscriptionService(SubscriptionPlanRepo planRepo, UserSubscriptionRepo userSubRepo) {
        this.planRepo = planRepo;
        this.userSubRepo = userSubRepo;
    }

    // Initialize plans on startup
    @PostConstruct
    public void initPlans() {
        if (planRepo.count() == 0) {
            List<SubscriptionPlan> plans = List.of(
                    new SubscriptionPlan(null, "Basic", BigDecimal.ZERO, 30,
                            List.of("5_messages/day", "1_appointment/week")),
                    new SubscriptionPlan(null, "Standard", new BigDecimal("9.99"), 30,
                            List.of("unlimited_chat", "3_appointments/week")),
                    new SubscriptionPlan(null, "Premium", new BigDecimal("19.99"), 30,
                            List.of("priority_support", "daily_appointments"))
            );
            planRepo.saveAll(plans);
        }
    }

    // Subscribe a user
    public UserSubscription subscribeUser(String userId, String planName) {
        SubscriptionPlan plan = planRepo.findByName(planName);
        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setPlanId(plan.getId());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
      //  subscription.setIsActive(true);
        return userSubRepo.save(subscription);
    }


    public List<SubscriptionPlan> getAllPlans() {
        return planRepo.findAll(); // Fetch all plans from MongoDB
    }
}

