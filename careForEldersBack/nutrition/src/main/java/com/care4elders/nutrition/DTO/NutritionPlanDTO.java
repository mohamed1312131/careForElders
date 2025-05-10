package com.care4elders.nutrition.DTO;

import java.util.List;

public class NutritionPlanDTO {
    private String id;
    private String meal;
    private String description;
    private int calories;
    private String pictureUrl;

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    private List<String> ingredients;     // ✅ New
    private List<String> comments;        // ✅ New
    private int likes;                    // ✅ New
    private int dislikes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String mealTime;
    private String notes;
    private String recommendedAgeGroup;

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRecommendedAgeGroup() {
        return recommendedAgeGroup;
    }

    public void setRecommendedAgeGroup(String recommendedAgeGroup) {
        this.recommendedAgeGroup = recommendedAgeGroup;
    }
// Getters and Setters
}
