package com.care4elders.event.controller;

import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.entity.Event;
import com.care4elders.event.service.EventService;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Event> addParticipant(@PathVariable String eventId, @PathVariable String userId) {
        return ResponseEntity.ok(eventService.addParticipant(eventId, userId));
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Event> removeParticipant(@PathVariable String eventId, @PathVariable String userId) {
        return ResponseEntity.ok(eventService.removeParticipant(eventId, userId));
    }
    @PostMapping("/{eventId}/register/{userId}")
    public ResponseEntity<?> registerForEvent(
            @PathVariable String eventId,
            @PathVariable String userId) {
        try {
            return ResponseEntity.ok(eventService.registerForEvent(eventId, userId));
        } catch (MessagingException | IOException | WriterException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing registration: " + e.getMessage());
        }
    }
    @PostMapping("/{eventId}/send-reminder")
    public ResponseEntity<String> sendReminder(@PathVariable String eventId) {
        try {
            eventService.sendManualReminder(eventId);
            return ResponseEntity.ok("Reminders sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending reminders: " + e.getMessage());
        }
    }
}