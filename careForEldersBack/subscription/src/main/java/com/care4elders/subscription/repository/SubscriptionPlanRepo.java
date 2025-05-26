package com.care4elders.subscription.repository;

import com.care4elders.subscription.entity.SubscriptionPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface SubscriptionPlanRepo extends MongoRepository<SubscriptionPlan, String> {
<<<<<<< Updated upstream
    SubscriptionPlan findByName(String name);
  //  List<SubscriptionPlan> findAll();
=======
    //SubscriptionPlan findByName(String name);
    List<SubscriptionPlan> findAll();
>>>>>>> Stashed changes

    Optional<SubscriptionPlan> findByName(String name);
}
