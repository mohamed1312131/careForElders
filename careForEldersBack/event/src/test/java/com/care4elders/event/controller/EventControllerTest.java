package com.care4elders.event.controller;

import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.entity.Event;
import com.care4elders.event.service.EventService;
import jakarta.mail.MessagingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEvent() {
        EventDTO dto = new EventDTO();
        Event event = new Event();
        when(eventService.createEvent(dto)).thenReturn(event);

        ResponseEntity<Event> response = eventController.createEvent(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService).createEvent(dto);
    }

    @Test
    public void testGetAllEvents() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(eventService.getAllEvents()).thenReturn(events);

        ResponseEntity<List<Event>> response = eventController.getAllEvents();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(events, response.getBody());
        verify(eventService).getAllEvents();
    }

    @Test
    public void testGetEventById() {
        String id = "1";
        Event event = new Event();
        when(eventService.getEventById(id)).thenReturn(event);

        ResponseEntity<Event> response = eventController.getEventById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService).getEventById(id);
    }

    @Test
    public void testUpdateEvent() {
        String id = "1";
        EventDTO dto = new EventDTO();
        Event event = new Event();
        when(eventService.updateEvent(id, dto)).thenReturn(event);

        ResponseEntity<Event> response = eventController.updateEvent(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService).updateEvent(id, dto);
    }

    @Test
    public void testDeleteEvent() {
        String id = "1";

        ResponseEntity<Void> response = eventController.deleteEvent(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(eventService).deleteEvent(id);
    }

    @Test
    public void testAddParticipant() {
        String eventId = "1";
        String userId = "2";
        Event event = new Event();
        when(eventService.addParticipant(eventId, userId)).thenReturn(event);

        ResponseEntity<Event> response = eventController.addParticipant(eventId, userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService).addParticipant(eventId, userId);
    }

    @Test
    public void testRemoveParticipant() {
        String eventId = "1";
        String userId = "2";
        Event event = new Event();
        when(eventService.removeParticipant(eventId, userId)).thenReturn(event);

        ResponseEntity<Event> response = eventController.removeParticipant(eventId, userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService).removeParticipant(eventId, userId);
    }

    @Test
    public void testRegisterForEvent_Success() throws Exception {
        String eventId = "1";
        String userId = "2";
        Event result = new Event();
        when(eventService.registerForEvent(eventId, userId)).thenReturn(result);

        ResponseEntity<?> response = eventController.registerForEvent(eventId, userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody());
        verify(eventService).registerForEvent(eventId, userId);
    }

    @Test
    public void testRegisterForEvent_Exception() throws Exception {
        String eventId = "1";
        String userId = "2";
        when(eventService.registerForEvent(eventId, userId))
                .thenThrow(new MessagingException("Mail error"));

        ResponseEntity<?> response = eventController.registerForEvent(eventId, userId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Mail error"));
        verify(eventService).registerForEvent(eventId, userId);
    }

    @Test
    public void testSendReminder_Success() throws Exception {
        String eventId = "1";
        doNothing().when(eventService).sendManualReminder(eventId);

        ResponseEntity<String> response = eventController.sendReminder(eventId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Reminders sent successfully", response.getBody());
        verify(eventService).sendManualReminder(eventId);
    }

    @Test
    public void testSendReminder_Exception() throws Exception {
        String eventId = "1";
        doThrow(new RuntimeException("Reminder error")).when(eventService).sendManualReminder(eventId);

        ResponseEntity<String> response = eventController.sendReminder(eventId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Reminder error"));
        verify(eventService).sendManualReminder(eventId);
    }
}
