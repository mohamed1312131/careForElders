package com.care4elders.event.controller;

import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.entity.Event;
import com.care4elders.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Event> registerForEvent(
            @PathVariable String eventId,
            @PathVariable String userId) {
        return ResponseEntity.ok(eventService.registerForEvent(eventId, userId));
    }
}