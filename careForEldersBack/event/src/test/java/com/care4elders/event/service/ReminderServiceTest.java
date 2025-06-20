package com.care4elders.event.service;

import com.care4elders.event.DTO.UserDTO;
import com.care4elders.event.client.UserServiceClient;
import com.care4elders.event.entity.Event;
import com.care4elders.event.entity.Reminder;
import com.care4elders.event.repository.EventRepository;
import com.care4elders.event.repository.ReminderRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ReminderServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private ReminderRepository reminderRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private ReminderService reminderService;

    @Captor
    private ArgumentCaptor<Reminder> reminderCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Field field = ReminderService.class.getDeclaredField("fromEmail");
            field.setAccessible(true);
            field.set(reminderService, "noreply@care4elders.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCheckForUpcomingEvents_NoEvents() {
        when(eventRepository.findByDateBetween(any(), any())).thenReturn(Collections.emptyList());

        reminderService.checkForUpcomingEvents();

        verify(eventRepository, times(1)).findByDateBetween(any(), any());
        verifyNoInteractions(reminderRepository, userServiceClient, mailSender);
    }

    @Test
    void testCheckForUpcomingEvents_ReminderAlreadySent() {
        Event event = new Event();
        event.setId("event1");
        event.setTitle("Yoga Class");
        event.setParticipantIds(Arrays.asList("user1"));
        List<Event> events = Collections.singletonList(event);

        when(eventRepository.findByDateBetween(any(), any())).thenReturn(events);
        when(reminderRepository.findByEventId("event1")).thenReturn(Collections.singletonList(new Reminder()));

        reminderService.checkForUpcomingEvents();

        verify(reminderRepository, times(1)).findByEventId("event1");
        verify(reminderRepository, never()).save(any());
        verifyNoInteractions(userServiceClient, mailSender);
    }

    @Test
    void testCheckForUpcomingEvents_SendsReminderAndSaves() throws Exception {
        Event event = new Event();
        event.setId("event2");
        event.setTitle("Doctor Visit");
        event.setParticipantIds(Arrays.asList("user2"));
        event.setDate(new Date());
        event.setLocation("Clinic");
        event.setDescription("Annual checkup");
        List<Event> events = Collections.singletonList(event);

        when(eventRepository.findByDateBetween(any(), any())).thenReturn(events);
        when(reminderRepository.findByEventId("event2")).thenReturn(Collections.emptyList());

        UserDTO user = new UserDTO();
        user.setId("user2");
        user.setFirstName("Alice");
        user.setEmail("alice@example.com");
        when(userServiceClient.getUserById("user2")).thenReturn(user);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        reminderService.checkForUpcomingEvents();

        verify(reminderRepository).save(reminderCaptor.capture());
        Reminder savedReminder = reminderCaptor.getValue();
        assertEquals("event2", savedReminder.getEventId());
        assertTrue(savedReminder.getMessage().contains("24-hour reminder sent"));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendRemindersForEvent_ExceptionInUserServiceClient() throws Exception {
        Event event = new Event();
        event.setId("event3");
        event.setTitle("Music Night");
        event.setParticipantIds(Arrays.asList("user3"));

        when(userServiceClient.getUserById("user3")).thenThrow(new RuntimeException("User not found"));

        java.lang.reflect.Method method = ReminderService.class.getDeclaredMethod("sendRemindersForEvent", Event.class);
        method.setAccessible(true);
        method.invoke(reminderService, event);

        verify(userServiceClient, times(1)).getUserById("user3");
        verifyNoInteractions(mailSender);
    }

    @Test
    void testSendReminderEmail_SendsCorrectly() throws Exception {
        UserDTO user = new UserDTO();
        user.setFirstName("Bob");
        user.setEmail("bob@example.com");

        Event event = new Event();
        event.setTitle("Gardening");
        event.setDate(new Date());
        event.setLocation("Community Park");
        event.setDescription("Spring planting");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        java.lang.reflect.Method method = ReminderService.class.getDeclaredMethod("sendReminderEmail", UserDTO.class, Event.class);
        method.setAccessible(true);
        method.invoke(reminderService, user, event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
