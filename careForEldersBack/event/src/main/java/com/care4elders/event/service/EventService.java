package com.care4elders.event.service;

import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.entity.Event;
import com.care4elders.event.exception.EventNotFoundException;
import com.care4elders.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public EventService(EventRepository eventRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
    }

    public Event createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDate(eventDTO.getDate());
        event.setLocation(eventDTO.getLocation());
        event.setDescription(eventDTO.getDescription());
        event.setParticipantIds(eventDTO.getParticipantIds());
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
    }

    public Event updateEvent(String id, EventDTO eventDTO) {
        Event existingEvent = getEventById(id);
        existingEvent.setTitle(eventDTO.getTitle());
        existingEvent.setDate(eventDTO.getDate());
        existingEvent.setLocation(eventDTO.getLocation());
        existingEvent.setDescription(eventDTO.getDescription());
        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }

    public Event addParticipant(String eventId, String userId) {
        Event event = getEventById(eventId);
        event.getParticipantIds().add(userId);
        return eventRepository.save(event);
    }

    public Event removeParticipant(String eventId, String userId) {
        Event event = getEventById(eventId);
        event.getParticipantIds().remove(userId);
        return eventRepository.save(event);
    }
    public Event registerForEvent(String eventId, String userId) {
        // 1. Get the event
        Event event = getEventById(eventId);

        // 2. Check if user is already registered
        if (event.getParticipantIds().contains(userId)) {
            throw new IllegalStateException("User is already registered for this event");
        }

        // 3. Add user to participants
        event.getParticipantIds().add(userId);

        // 4. Save and return the updated event
        return eventRepository.save(event);
    }
}