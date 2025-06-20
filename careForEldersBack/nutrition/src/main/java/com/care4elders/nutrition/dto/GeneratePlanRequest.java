package com.care4elders.nutrition.dto;

import java.util.List;

public class GeneratePlanRequest {
    private String userId;
    private List<String> medicalConditions;
    private String userEmail;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<String> getMedicalConditions() {
        return medicalConditions;
    }
    public void setMedicalConditions(List<String> medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
