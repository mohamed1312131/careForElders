package com.care4elders.event.service;

import com.care4elders.event.DTO.ReminderDTO;
import com.care4elders.event.entity.Reminder;
import com.care4elders.event.exception.ReminderNotFoundException;
import com.care4elders.event.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// this reminder part still need to work on it
@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    @Autowired
    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Reminder createReminder(ReminderDTO reminderDTO) {
        Reminder reminder = new Reminder();
        reminder.setMessage(reminderDTO.getMessage());
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setEventId(reminderDTO.getEventId());
        return reminderRepository.save(reminder);
    }

    public List<Reminder> getRemindersForEvent(String eventId) {
        return reminderRepository.findByEventId(eventId);
    }

    public void deleteReminder(String id) {
        reminderRepository.deleteById(id);
    }

    public void sendReminder(String reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with id: " + reminderId));
        // Implementation to send reminder (email/notification)
    }
}