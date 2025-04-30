package com.care4elders.subscription.RestController;

import com.care4elders.subscription.Service.SubscriptionService;
import com.care4elders.subscription.entity.SubscriptionPlan;
import com.care4elders.subscription.entity.UserSubscription;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@AllArgsConstructor

public class SubscriptionRestController {
    private final SubscriptionService subscriptionService;




    // Get all plans (Basic/Standard/Premium)
    @GetMapping("/plans")
    public List<SubscriptionPlan> getPlans() {
        return subscriptionService.getAllPlans();
    }

    // Subscribe a user
    @PostMapping("/subscribe")
    public UserSubscription subscribe(
            @RequestParam String userId,
            @RequestParam String planName
    ) {
        return subscriptionService.subscribeUser(userId, planName);
    }
}

