package com.care4elders.event.service;

import com.care4elders.event.DTO.EventDTO;
import com.care4elders.event.DTO.UserDTO;
import com.care4elders.event.client.UserServiceClient;
import com.care4elders.event.entity.Event;
import com.care4elders.event.exception.EventNotFoundException;
import com.care4elders.event.repository.EventRepository;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserServiceClient userServiceClient;
    private final QRCodeService qrCodeService;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EventService(EventRepository eventRepository,
                        UserServiceClient userServiceClient,
                        QRCodeService qrCodeService,
                        JavaMailSender mailSender) {
        this.eventRepository = eventRepository;
        this.userServiceClient = userServiceClient;
        this.qrCodeService = qrCodeService;
        this.mailSender = mailSender;
    }
    public List<Event> findEventsBetweenDates(Date start, Date end) {
        return eventRepository.findByDateBetween(start, end);
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
    public Event registerForEvent(String eventId, String userId) throws MessagingException, IOException, WriterException {
        // 1. Get the event
        Event event = getEventById(eventId);

        // 2. Check if user is already registered
        if (event.getParticipantIds().contains(userId)) {
            throw new IllegalStateException("User is already registered for this event");
        }

        // 3. Get user details
        UserDTO user = userServiceClient.getUserById(userId);

        // 4. Add user to participants
        event.getParticipantIds().add(userId);
        eventRepository.save(event);

        // 5. Generate QR code
        String qrContent = String.format("Event:%s|User:%s|Date:%s",
                event.getId(), user.getId(), new Date().toString());
        byte[] qrCode = qrCodeService.generateQRCodeImage(qrContent, 250, 250);

        // 6. Send confirmation email with QR code
        sendEventRegistrationEmail(user, event, qrCode);

        return event;
    }
    private void sendEventRegistrationEmail(UserDTO user, Event event, byte[] qrCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Event Registration Confirmation: " + event.getTitle());

        String emailContent = String.format(
                "<html><body>" +
                        "<h2>Hello %s %s,</h2>" +
                        "<p>You have successfully registered for the event: <b>%s</b></p>" +
                        "<p>Event Date: %s</p>" +
                        "<p>Location: %s</p>" +
                        "<p>Please find your QR code below which you'll need for event check-in:</p>" +
                        "<img src='cid:qrCode'/>" +
                        "<p>Thank you!</p>" +
                        "</body></html>",
                user.getFirstName(), user.getLastName(),
                event.getTitle(), event.getDate(), event.getLocation());

        helper.setText(emailContent, true);
        helper.addInline("qrCode", new ByteArrayResource(qrCode), "image/png");

        mailSender.send(message);
    }
    public void sendManualReminder(String eventId) {
        Event event = getEventById(eventId);
        for (String participantId : event.getParticipantIds()) {
            try {
                UserDTO user = userServiceClient.getUserById(participantId);
                sendReminderEmail(user, event);
            } catch (Exception e) {
                System.err.println("Failed to send manual reminder to user " + participantId +
                        " for event " + eventId + ": " + e.getMessage());
            }
        }
    }
    private void sendReminderEmail(UserDTO user, Event event) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Reminder: Upcoming Event - " + event.getTitle());

        String emailContent = String.format(
                "<html><body>" +
                        "<h2>Hello %s %s,</h2>" +
                        "<p>This is a friendly reminder about your upcoming event:</p>" +
                        "<h3>%s</h3>" +
                        "<p><strong>Date:</strong> %s</p>" +
                        "<p><strong>Location:</strong> %s</p>" +
                        "<p><strong>Description:</strong> %s</p>" +
                        "<p>We're looking forward to seeing you there!</p>" +
                        "</body></html>",
                user.getFirstName(), user.getLastName(),
                event.getTitle(), event.getDate(),
                event.getLocation(), event.getDescription());

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}