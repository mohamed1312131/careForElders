package com.care4elders.event.service;

import com.care4elders.event.DTO.UserDTO;
import com.care4elders.event.client.UserServiceClient;
import com.care4elders.event.entity.Event;
import com.care4elders.event.entity.Reminder;
import com.care4elders.event.repository.EventRepository;
import com.care4elders.event.repository.ReminderRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final EventRepository eventRepository;
    private final ReminderRepository reminderRepository;
    private final UserServiceClient userServiceClient;
    private final JavaMailSender mailSender;
    private final QRCodeService qrCodeService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public ReminderService(EventRepository eventRepository,
                           ReminderRepository reminderRepository,
                           UserServiceClient userServiceClient,
                           JavaMailSender mailSender,
                           QRCodeService qrCodeService) {
        this.eventRepository = eventRepository;
        this.reminderRepository = reminderRepository;
        this.userServiceClient = userServiceClient;
        this.mailSender = mailSender;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Checks for upcoming events every 1 minute and sends reminders 24 hours before.
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES) // Runs every one minute
    public void checkForUpcomingEvents() {
        try {
            Date now = new Date();
            Date twentyFourHoursFromNow = new Date(now.getTime() + TimeUnit.HOURS.toMillis(24));

            log.info("Checking for events between {} and {}", now, twentyFourHoursFromNow);

            // Find events happening exactly in the next 24 hours (±5 minutes window)
            List<Event> upcomingEvents = eventRepository.findByDateBetween(
                    new Date(now.getTime() - TimeUnit.MINUTES.toMillis(5)), // Small buffer
                    twentyFourHoursFromNow
            );

            if (upcomingEvents.isEmpty()) {
                log.info("No upcoming events found in the next 24 hours.");
                return;
            }

            log.info("Found {} events needing reminders", upcomingEvents.size());

            for (Event event : upcomingEvents) {
                try {
                    // Check if reminder was already sent (prevent duplicates)
                    if (reminderRepository.findByEventId(event.getId()).isEmpty()) {
                        log.info("Sending reminders for event: {}", event.getTitle());
                        sendRemindersForEvent(event);

                        // Record that we sent this reminder
                        Reminder reminder = new Reminder();
                        reminder.setEventId(event.getId());
                        reminder.setReminderDate(now);
                        reminder.setMessage("24-hour reminder sent for: " + event.getTitle());
                        reminderRepository.save(reminder);
                    } else {
                        log.info("Reminder already sent for event: {}", event.getTitle());
                    }
                } catch (Exception e) {
                    log.error("Failed to process reminders for event {}: {}", event.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error in reminder scheduler: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends reminder emails to all participants of an event
     */
    private void sendRemindersForEvent(Event event) {
        for (String participantId : event.getParticipantIds()) {
            try {
                UserDTO user = userServiceClient.getUserById(participantId);
                sendReminderEmail(user, event);
                log.info("Sent reminder to {} ({}) for event {}",
                        user.getEmail(), user.getFirstName(), event.getTitle());
            } catch (Exception e) {
                log.error("Failed to send reminder to user {} for event {}: {}",
                        participantId, event.getId(), e.getMessage());
            }
        }
    }

    /**
     * Builds and sends the actual reminder email
     */
    private void sendReminderEmail(UserDTO user, Event event) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("🔔 Reminder: " + event.getTitle() + " is tomorrow!");

        String emailContent = String.format(
                "<html>" +
                        "<body style='font-family: Arial, sans-serif;'>" +
                        "   <h2 style='color: #2c3e50;'>Hi %s,</h2>" +
                        "   <p>This is a friendly reminder about your upcoming event:</p>" +
                        "   <div style='background: #f8f9fa; padding: 20px; border-radius: 5px;'>" +
                        "       <h3 style='margin-top: 0; color: #3498db;'>%s</h3>" +
                        "       <p><strong>📅 Date:</strong> %s</p>" +
                        "       <p><strong>📍 Location:</strong> %s</p>" +
                        "       <p><strong>📝 Description:</strong> %s</p>" +
                        "   </div>" +
                        "   <p>We look forward to seeing you there!</p>" +
                        "   <p>Best regards,<br>Care4Elders Team</p>" +
                        "</body>" +
                        "</html>",
                user.getFirstName(),
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                event.getDescription()
        );

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}