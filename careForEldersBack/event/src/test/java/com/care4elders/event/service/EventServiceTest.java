package com.care4elders.event.service;

import java.util.Arrays;
import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.DTO.UserDTO;
import com.care4elders.event.client.UserServiceClient;
import com.care4elders.event.entity.Event;
import com.care4elders.event.exception.EventNotFoundException;
import com.care4elders.event.repository.EventRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private QRCodeService qrCodeService;
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Field field = EventService.class.getDeclaredField("fromEmail");
            field.setAccessible(true);
            field.set(eventService, "test@example.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindEventsBetweenDates() {
        Date start = new Date();
        Date end = new Date();
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(eventRepository.findByDateBetween(start, end)).thenReturn(events);

        List<Event> result = eventService.findEventsBetweenDates(start, end);

        assertEquals(2, result.size());
        verify(eventRepository).findByDateBetween(start, end);
    }

    @Test
    void testCreateEvent() {
        EventDTO dto = new EventDTO();
        dto.setTitle("Title");
        dto.setDate(new Date());
        dto.setLocation("Loc");
        dto.setDescription("Desc");
        dto.setParticipantIds(new ArrayList<>());
        Event savedEvent = new Event();
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.createEvent(dto);

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void testGetEventById_Found() {
        Event event = new Event();
        when(eventRepository.findById("1")).thenReturn(Optional.of(event));

        Event result = eventService.getEventById("1");

        assertEquals(event, result);
        verify(eventRepository).findById("1");
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById("1"));
    }

    @Test
    void testUpdateEvent() {
        Event existing = new Event();
        existing.setId("1");
        when(eventRepository.findById("1")).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenReturn(existing);

        EventDTO dto = new EventDTO();
        dto.setTitle("New Title");
        dto.setDate(new Date());
        dto.setLocation("New Loc");
        dto.setDescription("New Desc");

        Event result = eventService.updateEvent("1", dto);

        assertEquals(existing, result);
        verify(eventRepository).save(existing);
    }

    @Test
    void testDeleteEvent() {
        eventService.deleteEvent("1");
        verify(eventRepository).deleteById("1");
    }

    @Test
    void testAddParticipant() {
        Event event = new Event();
        event.setParticipantIds(new ArrayList<>());
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.addParticipant("e1", "u1");

        assertTrue(result.getParticipantIds().contains("u1"));
        verify(eventRepository).save(event);
    }

    @Test
    void testRemoveParticipant() {
        Event event = new Event();
        event.setParticipantIds(new ArrayList<>(Arrays.asList("u1", "u2")));
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.removeParticipant("e1", "u1");

        assertFalse(result.getParticipantIds().contains("u1"));
        verify(eventRepository).save(event);
    }

    @Test
    void testRegisterForEvent_Success() throws Exception {
        Event event = new Event();
        event.setId("e1");
        event.setParticipantIds(new ArrayList<>());
        event.setTitle("EventTitle");
        event.setDate(new Date());
        event.setLocation("Loc");
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        UserDTO user = new UserDTO();
        user.setId("u1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        when(userServiceClient.getUserById("u1")).thenReturn(user);

        byte[] qrBytes = new byte[]{1, 2, 3};
        when(qrCodeService.generateQRCodeImage(anyString(), eq(250), eq(250))).thenReturn(qrBytes);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Event result = eventService.registerForEvent("e1", "u1");

        assertTrue(result.getParticipantIds().contains("u1"));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testRegisterForEvent_AlreadyRegistered() {
        Event event = new Event();
        event.setId("e1");
        event.setParticipantIds(new ArrayList<>(Collections.singletonList("u1")));
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));

        assertThrows(IllegalStateException.class, () -> eventService.registerForEvent("e1", "u1"));
    }

    @Test
    void testSendManualReminder() throws Exception {
        Event event = new Event();
        event.setId("e1");
        event.setTitle("EventTitle");
        event.setDate(new Date());
        event.setLocation("Loc");
        event.setDescription("Desc");
        event.setParticipantIds(Arrays.asList("u1", "u2"));
        when(eventRepository.findById("e1")).thenReturn(Optional.of(event));

        UserDTO user1 = new UserDTO();
        user1.setId("u1");
        user1.setFirstName("A");
        user1.setLastName("B");
        user1.setEmail("a@b.com");
        UserDTO user2 = new UserDTO();
        user2.setId("u2");
        user2.setFirstName("C");
        user2.setLastName("D");
        user2.setEmail("c@d.com");
        when(userServiceClient.getUserById("u1")).thenReturn(user1);
        when(userServiceClient.getUserById("u2")).thenReturn(user2);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        eventService.sendManualReminder("e1");

        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }
}
