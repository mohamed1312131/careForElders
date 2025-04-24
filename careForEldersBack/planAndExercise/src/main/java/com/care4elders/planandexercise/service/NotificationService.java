package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.entity.Notification;
import com.care4elders.planandexercise.entity.NotificationType;
import com.care4elders.planandexercise.entity.Program;
import com.care4elders.planandexercise.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate;

    public void sendProgramAssignedNotification(String userId, Program program) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title("New Program Assigned")
                .message("You've been assigned to the program: " + program.getName())
                .type(NotificationType.PROGRAM_ASSIGNMENT)
                .build();

        notificationRepository.save(notification);
    }

    public void sendExerciseCompletedNotification(String doctorId, String patientName, String exerciseName) {
        Notification notification = Notification.builder()
                .userId(doctorId)
                .title("Exercise Completed")
                .message(patientName + " has completed exercise: " + exerciseName)
                .type(NotificationType.EXERCISE_COMPLETION)
                .build();

        notificationRepository.save(notification);
    }

    public void sendProgramCompletedNotification(String doctorId, String patientName, String programName) {
        Notification notification = Notification.builder()
                .userId(doctorId)
                .title("Program Completed")
                .message(patientName + " has completed the program: " + programName)
                .type(NotificationType.PROGRAM_COMPLETION)
                .build();

        notificationRepository.save(notification);
    }
}
