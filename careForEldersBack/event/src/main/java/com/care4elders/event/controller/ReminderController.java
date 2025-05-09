package com.care4elders.event.controller;

import com.care4elders.event.DTO.ReminderDTO;
import com.care4elders.event.entity.Reminder;
import com.care4elders.event.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    @Autowired
    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<Reminder> createReminder(@RequestBody ReminderDTO reminderDTO) {
        return new ResponseEntity<>(reminderService.createReminder(reminderDTO), HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Reminder>> getRemindersForEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(reminderService.getRemindersForEvent(eventId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable String id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Void> sendReminder(@PathVariable String id) {
        reminderService.sendReminder(id);
        return ResponseEntity.ok().build();
    }
}