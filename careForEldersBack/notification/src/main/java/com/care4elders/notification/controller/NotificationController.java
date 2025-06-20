package com.care4elders.notification.controller;

import com.care4elders.notification.dto.UserDto;
import com.care4elders.notification.entity.Notification;
import com.care4elders.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping("/{notificationId}/respond/{userId}")
    public ResponseEntity<Notification> respondToNotification(
            @PathVariable String notificationId,
            @PathVariable String userId,
            @RequestBody(required = false) Map<String, Object> requestBody) {

        Map<String, Object> locationData = null;
        if (requestBody != null && requestBody.containsKey("location")) {
            locationData = (Map<String, Object>) requestBody.get("location");
        }

        Notification notification = notificationService.respondToNotification(
                notificationId, userId, locationData);
        return ResponseEntity.ok(notification);
    }
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        UserDto testUser = new UserDto();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmergencyContactName("Emergency Contact");
        testUser.setEmergencyContactEmail("akr3m.homrani@gmail.com"); // Put a real email here

        Notification testNotif = new Notification();
        testNotif.setLatitude(40.7128);
        testNotif.setLongitude(-74.0060);
        testNotif.setAccuracy(50.0);
        testNotif.setLocationTimestamp(LocalDateTime.now());

        notificationService.sendEmergencyEmail(testUser, testNotif);
        return ResponseEntity.ok("Test email sent");
    }
}