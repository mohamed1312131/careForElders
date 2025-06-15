package com.care4elders.userservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "user_activity_logs")
public class UserActivityLog {

    @Id
    private String id;
    private String userId;
    private String activity;
    private String ipAddress;
    private LocalDateTime timestamp;

    public UserActivityLog() {}

    public UserActivityLog(String userId, String activity, String ipAddress, LocalDateTime timestamp) {
        this.userId = userId;
        this.activity = activity;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    // Getters and Setters...
}
