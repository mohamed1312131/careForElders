package com.care4elders.nutrition.service;

import com.care4elders.nutrition.dto.NutritionPlanDTO;
import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.entity.MealSchedule;

import java.util.List;
import java.util.Map;

public interface NutritionPlanService {

    // ================================
    // BASIC CRUD OPERATIONS
    // ================================

    NutritionPlanDTO createNutritionPlan(NutritionPlanDTO dto);

    List<NutritionPlanDTO> getAllNutritionPlans();
    List<NutritionPlanDTO> getAllNutritionPlans(int page, int size, String sortBy);

    NutritionPlanDTO getNutritionPlanById(String id);

    NutritionPlanDTO updateNutritionPlan(String id, NutritionPlanDTO planDTO);
    NutritionPlanDTO patchNutritionPlan(String id, Map<String, Object> updates);

    boolean deleteNutritionPlan(String id);

    // ================================
    // USER-SPECIFIC QUERIES
    // ================================

    List<NutritionPlanDTO> getNutritionPlansByUserId(String userId);
    List<NutritionPlanDTO> getNutritionPlansByUserId(String userId, int page, int size);

    // ================================
    // SEARCH OPERATIONS
    // ================================

    List<NutritionPlanDTO> getPlansByMedicalCondition(String condition, int page, int size);
    List<NutritionPlanDTO> searchPlans(String keyword, int page, int size);

    // ================================
    // STATISTICS
    // ================================

    long getTotalPlansCount();
    long getActivePlansCount();

    // ================================
    // AI-POWERED OPERATIONS
    // ================================

    NutritionPlanDTO generateMonthlyPlan(String userId, java.util.List<String> medicalConditions, String userEmail);
    NutritionPlanDTO regeneratePlan(String id);

    // ================================
    // SOCIAL FEATURES
    // ================================

    NutritionPlanDTO likeNutritionPlan(String id);
    NutritionPlanDTO dislikeNutritionPlan(String id);
    NutritionPlanDTO addComment(String id, String comment);
    NutritionPlanDTO addComment(String id, String comment, String userId);
    List<Map<String, Object>> getComments(String id);
    NutritionPlanDTO deleteComment(String id, String commentId);

    // ================================
    // EMAIL FEATURES
    // ================================

    NutritionPlanDTO toggleEmailReminders(String id, boolean enabled);
    NutritionPlanDTO updateMealSchedule(String id, MealSchedule schedule);
    boolean sendTestReminder(String id, String mealType);
    boolean sendImmediateReminder(String id, String mealType, String customMessage);

    // ================================
    // BULK OPERATIONS
    // ================================

    int bulkDeletePlans(List<String> planIds);
    int bulkUpdatePlans(List<String> planIds, Map<String, Object> updates);

    // ================================
    // UTILITY METHODS
    // ================================

    NutritionPlan findById(String id);
}