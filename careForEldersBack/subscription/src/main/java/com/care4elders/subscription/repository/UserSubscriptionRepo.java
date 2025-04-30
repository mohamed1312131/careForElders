package com.care4elders.subscription.repository;

import com.care4elders.subscription.entity.UserSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserSubscriptionRepo extends MongoRepository<UserSubscription, String> {
    UserSubscription findByUserId(String userId);
}
