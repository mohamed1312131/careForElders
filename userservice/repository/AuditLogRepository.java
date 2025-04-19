package com.care4elders.userservice.repository;

import com.care4elders.userservice.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findAllByUserId(String userId);
    List<AuditLog> findAllByUserIdAndAction(String userId, String action);
    List<AuditLog> findAllByTimestampBetween(Instant start, Instant end);
}
