package com.care4elders.nutrition.repository;

import com.care4elders.nutrition.entity.NutritionPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface NutritionPlanRepository extends MongoRepository<NutritionPlan, String> {
    // You can add custom queries here if needed later
}
