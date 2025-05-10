package com.care4elders.nutrition.service;

import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.repository.NutritionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NutritionPlanServiceImpl implements NutritionPlanService {

    @Autowired
    private NutritionPlanRepository repository;

    @Override
    public List<NutritionPlan> getAllPlans() {
        return repository.findAll();
    }

    @Override
    public NutritionPlan getPlanById(Long id) {
        return repository.findById(String.valueOf(id)).orElse(null);
    }

    @Override
    public NutritionPlan createPlan(NutritionPlan plan) {
        return repository.save(plan);
    }

    @Override
    public NutritionPlan updatePlan(Long id, NutritionPlan updatedPlan) {
        Optional<NutritionPlan> optional = repository.findById(String.valueOf(id));
        if (optional.isPresent()) {
            NutritionPlan existing = optional.get();
            existing.setMeal(updatedPlan.getMeal());
            existing.setDescription(updatedPlan.getDescription());
            existing.setCalories(updatedPlan.getCalories());
            existing.setPictureUrl(updatedPlan.getPictureUrl());
            existing.setMealTime(updatedPlan.getMealTime());
            existing.setNotes(updatedPlan.getNotes());
            existing.setRecommendedAgeGroup(updatedPlan.getRecommendedAgeGroup());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    public void deletePlan(Long id) {
        repository.deleteById(String.valueOf(id));
    }
}
