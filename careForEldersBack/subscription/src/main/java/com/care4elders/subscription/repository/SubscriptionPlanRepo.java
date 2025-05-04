package com.care4elders.subscription.repository;

import com.care4elders.subscription.entity.SubscriptionPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SubscriptionPlanRepo extends MongoRepository<SubscriptionPlan, String> {
    SubscriptionPlan findByName(String name);
}
