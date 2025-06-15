package com.care4elders.notification.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String id;
    private String userId;
    private String type = "activity_check";
    private String message;
    private LocalDateTime sentTime;
    private LocalDateTime responseDeadline;
    private boolean responded = false;
    private boolean active = true;
    private String response;

    // Add these new fields for location
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private LocalDateTime locationTimestamp;
}