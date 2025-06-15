package com.care4elders.nutrition.DTO;

import com.care4elders.nutrition.entity.MealSchedule;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class NutritionPlanDTO {

    private String id;
    private String userId;
    private String userEmail;

    @Size(max = 500, message = "Medical conditions should not exceed 500 characters")
    private String medicalConditions;

    @Size(max = 500, message = "Dietary preferences should not exceed 500 characters")
    private String dietaryPreferences;

    @Size(max = 500, message = "Allergies should not exceed 500 characters")
    private String allergies;

    private String aiGeneratedPlan;
    private String content; // Add this field for backward compatibility

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastReminderSent;

    private boolean active = true;
    private boolean emailRemindersEnabled = true;

    private Integer likes = 0;
    private Integer dislikes = 0;
    private List<Map<String, Object>> comments = new ArrayList<>();

    private MealSchedule mealSchedule;

    @Min(value = 1, message = "Plan duration must be at least 1 day")
    @Max(value = 365, message = "Plan duration cannot exceed 365 days")
    private Integer planDuration = 30;

    @Min(value = 800, message = "Target calories must be at least 800")
    @Max(value = 5000, message = "Target calories cannot exceed 5000")
    private Integer targetCalories = 2000;

    // Constructors
    public NutritionPlanDTO() {}

    public NutritionPlanDTO(String userId, String userEmail, String medicalConditions,
                            String dietaryPreferences, String allergies) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.medicalConditions = medicalConditions;
        this.dietaryPreferences = dietaryPreferences;
        this.allergies = allergies;
        this.active = true;
        this.emailRemindersEnabled = true;
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<>();
        this.planDuration = 30;
        this.targetCalories = 2000;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public String getDietaryPreferences() { return dietaryPreferences; }
    public void setDietaryPreferences(String dietaryPreferences) { this.dietaryPreferences = dietaryPreferences; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getAiGeneratedPlan() { return aiGeneratedPlan; }
    public void setAiGeneratedPlan(String aiGeneratedPlan) { this.aiGeneratedPlan = aiGeneratedPlan; }

    // Add content getter and setter for backward compatibility
    public String getContent() {
        // Return aiGeneratedPlan if content is null
        return content != null ? content : aiGeneratedPlan;
    }

    public void setContent(String content) {
        this.content = content;
        // Also set aiGeneratedPlan for consistency
        if (this.aiGeneratedPlan == null) {
            this.aiGeneratedPlan = content;
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastReminderSent() { return lastReminderSent; }
    public void setLastReminderSent(LocalDateTime lastReminderSent) { this.lastReminderSent = lastReminderSent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isEmailRemindersEnabled() { return emailRemindersEnabled; }
    public void setEmailRemindersEnabled(boolean emailRemindersEnabled) { this.emailRemindersEnabled = emailRemindersEnabled; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public Integer getDislikes() { return dislikes; }
    public void setDislikes(Integer dislikes) { this.dislikes = dislikes; }

    public List<Map<String, Object>> getComments() { return comments; }
    public void setComments(List<Map<String, Object>> comments) { this.comments = comments; }

    public MealSchedule getMealSchedule() { return mealSchedule; }
    public void setMealSchedule(MealSchedule mealSchedule) { this.mealSchedule = mealSchedule; }

    public Integer getPlanDuration() { return planDuration; }
    public void setPlanDuration(Integer planDuration) { this.planDuration = planDuration; }

    public Integer getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Integer targetCalories) { this.targetCalories = targetCalories; }

    // Utility methods
    public void addComment(Map<String, Object> comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }

    public void incrementLikes() {
        this.likes = (this.likes != null) ? this.likes + 1 : 1;
    }

    public void incrementDislikes() {
        this.dislikes = (this.dislikes != null) ? this.dislikes + 1 : 1;
    }

    public boolean hasEmailReminders() {
        return emailRemindersEnabled && userEmail != null && !userEmail.trim().isEmpty();
    }

    public boolean isValidForCreation() {
        return userId != null && !userId.trim().isEmpty() &&
                userEmail != null && !userEmail.trim().isEmpty() &&
                medicalConditions != null && !medicalConditions.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "NutritionPlanDTO{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", medicalConditions='" + medicalConditions + '\'' +
                ", dietaryPreferences='" + dietaryPreferences + '\'' +
                ", allergies='" + allergies + '\'' +
                ", active=" + active +
                ", emailRemindersEnabled=" + emailRemindersEnabled +
                ", planDuration=" + planDuration +
                ", targetCalories=" + targetCalories +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}