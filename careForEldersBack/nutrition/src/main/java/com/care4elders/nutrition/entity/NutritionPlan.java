package com.care4elders.nutrition.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "nutrition_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionPlan {

    @Id
    private String id;

    private String userId;
    private String userEmail;
    private String medicalConditions;
    private String dietaryPreferences;
    private String allergies;
    private String aiGeneratedPlan;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastReminderSent;

    private boolean active;
    private boolean emailRemindersEnabled;

    private Integer likes;
    private Integer dislikes;
    private List<Map<String, Object>> comments;

    // --- Added fields for meal plan details ---
    private String meal;
    private String description;
    private Integer calories;
    private String mealTime;
    private String notes;
    private String recommendedAgeGroup;
    private String pictureUrl;
    private java.util.List<String> ingredients;
    // --- End added fields ---

    private MealSchedule mealSchedule;
    private Integer planDuration; // in days
    private Integer targetCalories; // daily target calories
}