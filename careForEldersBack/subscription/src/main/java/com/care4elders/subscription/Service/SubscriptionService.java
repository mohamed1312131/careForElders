package com.care4elders.subscription.Service;

import com.care4elders.subscription.DTO.SubscriptionPlanDTO;
import com.care4elders.subscription.DTO.UserDTO;
import com.care4elders.subscription.DTO.UserSubscriptionDTO;
import com.care4elders.subscription.entity.SubscriptionPlan;
import com.care4elders.subscription.entity.UserSubscription;
import com.care4elders.subscription.exception.EntityNotFoundException;
import com.care4elders.subscription.exception.ServiceUnavailableException;
import com.care4elders.subscription.repository.SubscriptionPlanRepo;
import com.care4elders.subscription.repository.UserSubscriptionRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionPlanRepo subscriptionPlanRepository;
    private final UserSubscriptionRepo userSubscriptionRepository;
    private final RestTemplate restTemplate;

    // Subscription Plan CRUD Operations
    public SubscriptionPlan createPlan(String adminId, SubscriptionPlanDTO planDTO) {
        validateAdmin(adminId);
        SubscriptionPlan plan = new SubscriptionPlan();
        // Map DTO to entity
        plan.setName(planDTO.getName());
        plan.setPrice(planDTO.getPrice());
        plan.setDurationDays(planDTO.getDurationDays());
        plan.setFeatures(planDTO.getFeatures());
        return subscriptionPlanRepository.save(plan);
    }

    public SubscriptionPlan updatePlan(String adminId, String planId, SubscriptionPlanDTO planDTO) {
        validateAdmin(adminId);
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));
        // Update fields
        existingPlan.setName(planDTO.getName());
        existingPlan.setPrice(planDTO.getPrice());
        existingPlan.setDurationDays(planDTO.getDurationDays());
        existingPlan.setFeatures(planDTO.getFeatures());
        return subscriptionPlanRepository.save(existingPlan);
    }

    public void deletePlan(String adminId, String planId) {
        validateAdmin(adminId);
        subscriptionPlanRepository.deleteById(planId);
    }

    // User Subscription Management
    public UserSubscription assignPlanToUser(UserSubscriptionDTO subscriptionDTO) {
        validateUser(subscriptionDTO.getUserId());
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        // Deactivate any existing active subscription
        userSubscriptionRepository.findByUserIdAndIsActive(subscriptionDTO.getUserId(), true)
                .ifPresent(existing -> {
                    existing.setActive(false);
                    userSubscriptionRepository.save(existing);
                });

        UserSubscription newSubscription = new UserSubscription();
        newSubscription.setUserId(subscriptionDTO.getUserId());
        newSubscription.setPlanId(subscriptionDTO.getPlanId());
        newSubscription.setStartDate(LocalDateTime.now());
        newSubscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        newSubscription.setActive(true);

        return userSubscriptionRepository.save(newSubscription);
    }

    public List<UserSubscription> getUserSubscriptions(String userId) {
        validateUser(userId);
        return userSubscriptionRepository.findByUserId(userId);
    }

    private void validateAdmin(String adminId) {
        try {
            UserDTO admin = restTemplate.getForObject(
                    "http://USER-SERVICE/users/{Id}",
                    UserDTO.class,
                    adminId
            );

            if (admin == null || !"ADMINISTRATOR".equals(admin.getRole())) {
                throw new EntityNotFoundException("Admin not found or invalid role");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Admin not found with ID: " + adminId);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("User service unavailable");
        }
    }

    private void validateUser(String userId) {
        try {
            UserDTO user = restTemplate.getForObject(
                    "http://USER-SERVICE/users/{Id}",
                    UserDTO.class,
                    userId
            );

            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
}