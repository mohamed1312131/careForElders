package com.care4elders.notification.service;

import com.care4elders.notification.client.UserServiceClient;
import com.care4elders.notification.dto.UserDto;
import com.care4elders.notification.entity.Notification;
import com.care4elders.notification.repository.NotificationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserServiceClient userServiceClient;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(fixedRate = 2 * 60 * 1000) // 2 minutes
    public void sendActivityCheckNotifications() {
        try {
            List<String> allUserIds = userServiceClient.getAllUserIds();

            if (allUserIds.isEmpty()) {
                System.out.println("No users found or user service unavailable");
                return;
            }

            for (String userId : allUserIds) {
                // Find or create the notification for this user
                Notification notification = notificationRepository
                        .findByUserIdAndType(userId, "activity_check")
                        .orElse(new Notification());

                // Skip if there's an active, unresponded notification
                if (notification.isActive() && !notification.isResponded()) {
                    System.out.println("User " + userId + " has pending notification");
                    continue;
                }

                // Update existing notification
                notification.setUserId(userId);
                notification.setType("activity_check");
                notification.setMessage("Please confirm you're OK within 10 minutes");
                notification.setSentTime(LocalDateTime.now());
                notification.setResponseDeadline(LocalDateTime.now().plusMinutes(2));
                notification.setResponded(false);
                notification.setActive(true);
                notification.setResponse(null);

                notificationRepository.save(notification);
                System.out.println("Updated notification for user: " + userId);
            }
        } catch (FeignException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void checkUnrespondedNotifications() {
        System.out.println("Checking for unresponded notifications at " + LocalDateTime.now());

        List<Notification> unresponded = notificationRepository
                .findByRespondedFalseAndResponseDeadlineBefore(LocalDateTime.now());

        System.out.println("Found " + unresponded.size() + " unresponded notifications");

        for (Notification notification : unresponded) {
            System.out.println("Processing unresponded notification for user: " + notification.getUserId());

            notification.setActive(false);
            notificationRepository.save(notification);

            try {
                UserDto user = userServiceClient.getUserEmergencyInfo(notification.getUserId());
                if (user == null) {
                    System.err.println("User not found: " + notification.getUserId());
                    continue;
                }

                if (user.getEmergencyContactEmail() == null) {
                    System.err.println("No emergency contact email for user: " + notification.getUserId());
                    continue;
                }

                System.out.println("Sending emergency email for user: " + notification.getUserId());
                sendEmergencyEmail(user, notification);
            } catch (FeignException e) {
                System.err.println("Error fetching user details: " + e.getMessage());
            }
        }
    }

    public Notification respondToNotification(String notificationId, String userId, Map<String, Object> locationData) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.isResponded()) {
            throw new RuntimeException("Notification already responded");
        }

        if (LocalDateTime.now().isAfter(notification.getResponseDeadline())) {
            throw new RuntimeException("Response deadline passed");
        }

        notification.setResponded(true);
        notification.setResponse("confirmed");
        notification.setActive(false);

        // Store location data if provided
        if (locationData != null) {
            notification.setLatitude((Double) locationData.get("latitude"));
            notification.setLongitude((Double) locationData.get("longitude"));
            notification.setAccuracy((Double) locationData.get("accuracy"));
            notification.setLocationTimestamp(LocalDateTime.now());
        }

        return notificationRepository.save(notification);
    }

    public void sendEmergencyEmail(UserDto user, Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmergencyContactEmail());
            message.setSubject("Urgent: Please check on " + user.getFirstName());

            String locationInfo = "";
            if (notification.getLatitude() != null && notification.getLongitude() != null) {
                locationInfo = String.format(
                        "\n\nLast known location (at %s):\n" +
                                "https://maps.google.com/?q=%f,%f\n" +
                                "Accuracy: %.2f meters",
                        notification.getLocationTimestamp(),
                        notification.getLatitude(),
                        notification.getLongitude(),
                        notification.getAccuracy()
                );
            }

            message.setText(String.format(
                    "Dear %s,\n\n" +
                            "Your friend/family member %s %s has not responded to our wellness check.\n" +
                            "Please check on them as soon as possible.%s\n\n" +
                            "Best regards,\nCare4Elders Team",
                    user.getEmergencyContactName(),
                    user.getFirstName(),
                    user.getLastName(),
                    locationInfo
            ));

            mailSender.send(message);
            System.out.println("Emergency email sent to: " + user.getEmergencyContactEmail());
        } catch (Exception e) {
            System.err.println("Failed to send emergency email: " + e.getMessage());
        }
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdAndActiveTrue(userId);
    }
}