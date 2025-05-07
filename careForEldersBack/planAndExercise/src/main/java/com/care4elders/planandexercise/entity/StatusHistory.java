package com.care4elders.planandexercise.entity;

import java.time.LocalDateTime;

public class StatusHistory {
    private String status;
    private LocalDateTime changedAt;
    private String changedBy; // User ID
    private String notes;
}