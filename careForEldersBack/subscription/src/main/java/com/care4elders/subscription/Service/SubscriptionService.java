package com.care4elders.subscription.Service;

import com.care4elders.subscription.DTO.SubscriptionPlanDTO;
import com.care4elders.subscription.DTO.UserDTO;
import com.care4elders.subscription.DTO.UserSubscriptionDTO;
import com.care4elders.subscription.DTO.UserSubscriptionResponseDTO;
import com.care4elders.subscription.entity.SubscriptionPlan;
import com.care4elders.subscription.entity.UserSubscription;
import com.care4elders.subscription.exception.EntityNotFoundException;
import com.care4elders.subscription.exception.ServiceUnavailableException;
import com.care4elders.subscription.repository.SubscriptionPlanRepo;
import com.care4elders.subscription.repository.UserSubscriptionRepo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionPlanRepo subscriptionPlanRepository;
    private final UserSubscriptionRepo userSubscriptionRepository;
    private final RestTemplate restTemplate;

    // Admin operations
    public SubscriptionPlan createPlan(String adminId, SubscriptionPlanDTO planDTO) {
        validateAdmin(adminId);
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(planDTO.getName());
        plan.setPrice(planDTO.getPrice());
        plan.setDurationDays(planDTO.getDurationDays());
        plan.setFeatures(planDTO.getFeatures());
        return subscriptionPlanRepository.save(plan);
    }
    public SubscriptionPlan getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan updatePlan(String adminId, String planId, SubscriptionPlanDTO planDTO) {
        validateAdmin(adminId);
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));
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

    // Assign user to a plan
    public UserSubscription assignPlanToUser(UserSubscriptionDTO subscriptionDTO) {
        validateUser(subscriptionDTO.getUserId());
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        // Deactivate existing active subscription
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

<<<<<<< Updated upstream
    public List<UserSubscription> getUserSubscriptions(String userId) {
        validateUser(userId);
        return userSubscriptionRepository.findByUserId(userId);
=======
    public UserSubscription assignDefaultPlan(String userId) {
        if (userSubscriptionRepository.existsByUserIdAndIsActive(userId, true)) {
            throw new RuntimeException("User already has an active subscription");
        }

        SubscriptionPlan basicPlan = subscriptionPlanRepository.findByName("BASIC")
                .orElseThrow(() -> new RuntimeException("Basic plan not found"));

        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setPlanId(basicPlan.getId());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(basicPlan.getDurationDays()));
        subscription.setActive(true);

        return userSubscriptionRepository.save(subscription);
>>>>>>> Stashed changes
    }

    public List<UserSubscriptionResponseDTO> getSubscriptionsByUser(String userId) {
        return userSubscriptionRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public UserSubscriptionResponseDTO getCurrentSubscriptionForUser(String userId) {
        return userSubscriptionRepository.findByUserIdAndIsActive(userId, true)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    private UserSubscriptionResponseDTO mapToResponseDTO(UserSubscription subscription) {
        if (subscription == null) {
            return null;
        }

        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + subscription.getPlanId()));

        return UserSubscriptionResponseDTO.builder()
                .userId(subscription.getUserId())
                .planId(subscription.getPlanId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .active(subscription.isActive())
                .planName(plan.getName())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .features(plan.getFeatures())
                .build();
    }

    // Internal validators
    private void validateAdmin(String adminId) {
        try {
            UserDTO admin = restTemplate.getForObject("http://USER-SERVICE/users/{Id}", UserDTO.class, adminId);
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
            UserDTO user = restTemplate.getForObject("http://USER-SERVICE/users/{Id}", UserDTO.class, userId);
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