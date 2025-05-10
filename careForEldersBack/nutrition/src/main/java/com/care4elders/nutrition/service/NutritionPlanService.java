package com.care4elders.nutrition.service;

import com.care4elders.nutrition.entity.NutritionPlan;

import java.util.List;

public interface NutritionPlanService {
    List<NutritionPlan> getAllPlans();
    NutritionPlan getPlanById(Long id);
    NutritionPlan createPlan(NutritionPlan plan);
    NutritionPlan updatePlan(Long id, NutritionPlan updatedPlan);
    void deletePlan(Long id);
}
