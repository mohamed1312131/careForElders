package com.care4elders.notification.repository;

import com.care4elders.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdAndActiveTrue(String userId);
    List<Notification> findByRespondedFalseAndResponseDeadlineBefore(LocalDateTime now);
    List<Notification> findByTypeAndRespondedFalse(String type);
    Optional<Notification> findByIdAndUserId(String id, String userId);
    Optional<Notification> findTopByUserIdOrderBySentTimeDesc(String userId);
    Optional<Notification> findByUserIdAndType(String userId, String type);

}