package com.care4elders.planandexercise.controller;

import com.care4elders.planandexercise.entity.Notification;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @PatchMapping("/{id}/mark-read")
    public Notification markAsRead(@PathVariable String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
