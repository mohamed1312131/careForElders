package com.care4elders.subscription.repository;

import com.care4elders.subscription.entity.UserSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserSubscriptionRepo extends MongoRepository<UserSubscription, String> {
    List<UserSubscription> findByUserId(String userId);
    Optional<UserSubscription> findByUserIdAndIsActive(String userId, boolean isActive);
}
