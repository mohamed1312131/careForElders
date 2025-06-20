package com.care4elders.notification.service;

import com.care4elders.notification.client.UserServiceClient;
import com.care4elders.notification.dto.UserDto;
import com.care4elders.notification.entity.Notification;
import com.care4elders.notification.repository.NotificationRepository;
import feign.FeignException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JavaMailSender mailSender;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(notificationService, "fromEmail", "test@care4elders.com");
    }

    @Test
    public void testSendActivityCheckNotifications_NoUsers() {
        when(userServiceClient.getAllUserIds()).thenReturn(Collections.emptyList());
        notificationService.sendActivityCheckNotifications();
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testSendActivityCheckNotifications_FirstTimeNotification() {
        List<String> userIds = Arrays.asList("user1");
        when(userServiceClient.getAllUserIds()).thenReturn(userIds);
        when(notificationRepository.findTopByUserIdOrderBySentTimeDesc("user1")).thenReturn(Optional.empty());

        notificationService.sendActivityCheckNotifications();

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testSendActivityCheckNotifications_ActiveUnrespondedNotification() {
        List<String> userIds = Arrays.asList("user1");
        Notification notification = new Notification();
        notification.setActive(true);
        notification.setResponded(false);

        when(userServiceClient.getAllUserIds()).thenReturn(userIds);
        when(notificationRepository.findTopByUserIdOrderBySentTimeDesc("user1")).thenReturn(Optional.of(notification));

        notificationService.sendActivityCheckNotifications();

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testSendActivityCheckNotifications_CreateNewNotification() {
        List<String> userIds = Arrays.asList("user1");
        Notification notification = new Notification();
        notification.setActive(false);
        notification.setResponded(true);

        when(userServiceClient.getAllUserIds()).thenReturn(userIds);
        when(notificationRepository.findTopByUserIdOrderBySentTimeDesc("user1")).thenReturn(Optional.of(notification));

        notificationService.sendActivityCheckNotifications();

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testSendActivityCheckNotifications_UserServiceThrowsFeignException() {
        when(userServiceClient.getAllUserIds()).thenThrow(mock(FeignException.class));
        notificationService.sendActivityCheckNotifications();
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testCheckUnrespondedNotifications_SendsEmergencyEmail() {
        Notification notification = new Notification();
        notification.setUserId("user1");
        notification.setActive(true);
        notification.setResponded(false);
        notification.setResponseDeadline(LocalDateTime.now().minusMinutes(2));

        List<Notification> notifications = Arrays.asList(notification);

        UserDto userDto = new UserDto();
        userDto.setEmergencyContactEmail("emergency@contact.com");
        userDto.setEmergencyContactName("John Doe");
        userDto.setFirstName("Jane");
        userDto.setLastName("Smith");

        when(notificationRepository.findByRespondedFalseAndResponseDeadlineBefore(any(LocalDateTime.class)))
                .thenReturn(notifications);
        when(userServiceClient.getUserEmergencyInfo("user1")).thenReturn(userDto);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.checkUnrespondedNotifications();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testCheckUnrespondedNotifications_NoEmergencyContactEmail() {
        Notification notification = new Notification();
        notification.setUserId("user1");
        notification.setActive(true);
        notification.setResponded(false);
        notification.setResponseDeadline(LocalDateTime.now().minusMinutes(2));

        List<Notification> notifications = Arrays.asList(notification);

        UserDto userDto = new UserDto();
        userDto.setEmergencyContactEmail(null);

        when(notificationRepository.findByRespondedFalseAndResponseDeadlineBefore(any(LocalDateTime.class)))
                .thenReturn(notifications);
        when(userServiceClient.getUserEmergencyInfo("user1")).thenReturn(userDto);

        notificationService.checkUnrespondedNotifications();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testCheckUnrespondedNotifications_UserServiceThrowsFeignException() {
        Notification notification = new Notification();
        notification.setUserId("user1");
        notification.setActive(true);
        notification.setResponded(false);
        notification.setResponseDeadline(LocalDateTime.now().minusMinutes(2));

        List<Notification> notifications = Arrays.asList(notification);

        when(notificationRepository.findByRespondedFalseAndResponseDeadlineBefore(any(LocalDateTime.class)))
                .thenReturn(notifications);
        when(userServiceClient.getUserEmergencyInfo("user1")).thenThrow(mock(FeignException.class));

        notificationService.checkUnrespondedNotifications();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testRespondToNotification_SuccessWithLocation() {
        Notification notification = new Notification();
        notification.setResponded(false);
        notification.setResponseDeadline(LocalDateTime.now().plusMinutes(1));
        notification.setActive(true);

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", 10.0);
        locationData.put("longitude", 20.0);
        locationData.put("accuracy", 5.0);

        when(notificationRepository.findByIdAndUserId("notif1", "user1"))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.respondToNotification("notif1", "user1", locationData);

        assertTrue(result.isResponded());
        assertEquals("confirmed", result.getResponse());
        assertFalse(result.isActive());
        assertEquals(Double.valueOf(10.0), result.getLatitude());
        assertEquals(Double.valueOf(20.0), result.getLongitude());
        assertEquals(Double.valueOf(5.0), result.getAccuracy());
        assertNotNull(result.getLocationTimestamp());
    }

    @Test(expected = RuntimeException.class)
    public void testRespondToNotification_AlreadyResponded() {
        Notification notification = new Notification();
        notification.setResponded(true);

        when(notificationRepository.findByIdAndUserId("notif1", "user1"))
                .thenReturn(Optional.of(notification));

        notificationService.respondToNotification("notif1", "user1", null);
    }

    @Test(expected = RuntimeException.class)
    public void testRespondToNotification_DeadlinePassed() {
        Notification notification = new Notification();
        notification.setResponded(false);
        notification.setResponseDeadline(LocalDateTime.now().minusMinutes(1));

        when(notificationRepository.findByIdAndUserId("notif1", "user1"))
                .thenReturn(Optional.of(notification));

        notificationService.respondToNotification("notif1", "user1", null);
    }

    @Test(expected = RuntimeException.class)
    public void testRespondToNotification_NotificationNotFound() {
        when(notificationRepository.findByIdAndUserId("notif1", "user1"))
                .thenReturn(Optional.empty());

        notificationService.respondToNotification("notif1", "user1", null);
    }

    @Test
    public void testSendEmergencyEmail_WithLocation() {
        UserDto user = new UserDto();
        user.setEmergencyContactEmail("emergency@contact.com");
        user.setEmergencyContactName("John Doe");
        user.setFirstName("Jane");
        user.setLastName("Smith");

        Notification notification = new Notification();
        notification.setLatitude(10.123456);
        notification.setLongitude(20.654321);
        notification.setAccuracy(5.0);
        notification.setLocationTimestamp(LocalDateTime.now());

        notificationService.sendEmergencyEmail(user, notification);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmergencyEmail_WithoutLocation() {
        UserDto user = new UserDto();
        user.setEmergencyContactEmail("emergency@contact.com");
        user.setEmergencyContactName("John Doe");
        user.setFirstName("Jane");
        user.setLastName("Smith");

        Notification notification = new Notification();

        notificationService.sendEmergencyEmail(user, notification);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testGetUserNotifications() {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationRepository.findByUserIdAndActiveTrue("user1")).thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications("user1");

        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByUserIdAndActiveTrue("user1");
    }
}
