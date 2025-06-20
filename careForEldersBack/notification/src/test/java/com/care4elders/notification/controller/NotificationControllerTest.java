package com.care4elders.notification.controller;

import com.care4elders.notification.dto.UserDto;
import com.care4elders.notification.entity.Notification;
import com.care4elders.notification.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserNotifications() {
        String userId = "user123";
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationService.getUserNotifications(userId)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getUserNotifications(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notifications, response.getBody());
        verify(notificationService, times(1)).getUserNotifications(userId);
    }

    @Test
    public void testRespondToNotification_WithLocation() {
        String notificationId = "notif1";
        String userId = "user1";
        Map<String, Object> location = new HashMap<>();
        location.put("latitude", 1.0);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("location", location);

        Notification notification = new Notification();
        when(notificationService.respondToNotification(notificationId, userId, location)).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.respondToNotification(notificationId, userId, requestBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notification, response.getBody());
        verify(notificationService, times(1)).respondToNotification(notificationId, userId, location);
    }

    @Test
    public void testRespondToNotification_WithoutLocation() {
        String notificationId = "notif2";
        String userId = "user2";
        Map<String, Object> requestBody = new HashMap<>();

        Notification notification = new Notification();
        when(notificationService.respondToNotification(notificationId, userId, null)).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.respondToNotification(notificationId, userId, requestBody);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notification, response.getBody());
        verify(notificationService, times(1)).respondToNotification(notificationId, userId, null);
    }

    @Test
    public void testRespondToNotification_NullRequestBody() {
        String notificationId = "notif3";
        String userId = "user3";

        Notification notification = new Notification();
        when(notificationService.respondToNotification(notificationId, userId, null)).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.respondToNotification(notificationId, userId, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(notification, response.getBody());
        verify(notificationService, times(1)).respondToNotification(notificationId, userId, null);
    }

    @Test
    public void testTestEmail() {
        doNothing().when(notificationService).sendEmergencyEmail(any(UserDto.class), any(Notification.class));

        ResponseEntity<String> response = notificationController.testEmail();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test email sent", response.getBody());
        verify(notificationService, times(1)).sendEmergencyEmail(any(UserDto.class), any(Notification.class));
    }
}
