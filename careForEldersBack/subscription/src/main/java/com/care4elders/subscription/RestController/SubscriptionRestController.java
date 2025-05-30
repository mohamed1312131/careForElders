package com.care4elders.subscription.RestController;

import com.care4elders.subscription.DTO.SubscriptionPlanDTO;
import com.care4elders.subscription.DTO.UserSubscriptionDTO;
import com.care4elders.subscription.DTO.UserSubscriptionResponseDTO;
import com.care4elders.subscription.Service.SubscriptionService;
import com.care4elders.subscription.entity.SubscriptionPlan;
import com.care4elders.subscription.entity.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionRestController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionPlan createPlan(@RequestHeader("X-Admin-Id") String adminId,
                                       @RequestBody SubscriptionPlanDTO planDTO) {
        return subscriptionService.createPlan(adminId, planDTO);
    }

    @GetMapping("/plans")
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionService.getAllPlans();
    }

    @PutMapping("/plans/{planId}")
    public SubscriptionPlan updatePlan(@RequestHeader("X-Admin-Id") String adminId,
                                       @PathVariable String planId,
                                       @RequestBody SubscriptionPlanDTO planDTO) {
        return subscriptionService.updatePlan(adminId, planId, planDTO);
    }

    @DeleteMapping("/plans/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@RequestHeader("X-Admin-Id") String adminId,
                           @PathVariable String planId) {
        subscriptionService.deletePlan(adminId, planId);
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscription assignPlan(@RequestBody UserSubscriptionDTO subscriptionDTO) {
        return subscriptionService.assignPlanToUser(subscriptionDTO);
    }

    @PostMapping("/assign-default/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscription assignDefaultPlan(@PathVariable String userId) {
        return subscriptionService.assignDefaultPlan(userId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSubscriptionResponseDTO>> getSubscriptionsByUser(@PathVariable String userId) {
        List<UserSubscriptionResponseDTO> userSubscriptions = subscriptionService.getSubscriptionsByUser(userId);
        return new ResponseEntity<>(userSubscriptions, HttpStatus.OK);
    }

    @GetMapping("/current/{userId}")
    public ResponseEntity<UserSubscriptionResponseDTO> getCurrentSubscription(@PathVariable String userId) {
        UserSubscriptionResponseDTO response = subscriptionService.getCurrentSubscriptionForUser(userId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
