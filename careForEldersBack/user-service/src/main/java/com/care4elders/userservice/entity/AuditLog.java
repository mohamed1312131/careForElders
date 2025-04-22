package com.care4elders.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;

    private String userId;
    private String action;           // e.g. LOGIN_SUCCESS, PASSWORD_RESET
    private String ipAddress;        // optional
    private String userAgent;        // optional
    private Instant timestamp;
}
