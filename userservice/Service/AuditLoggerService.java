package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.AuditLog;
import com.care4elders.userservice.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLoggerService {

    private final AuditLogRepository auditLogRepository;

    public void log(String userId, String action, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .ipAddress(ip)
                .userAgent(userAgent)
                .timestamp(Instant.now())
                .build();

        auditLogRepository.save(log);
    }

    // Overload if no request context
    public void log(String userId, String action) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .timestamp(Instant.now())
                .build();

        auditLogRepository.save(log);
    }
}
