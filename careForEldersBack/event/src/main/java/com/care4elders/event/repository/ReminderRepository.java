package com.care4elders.event.repository;

import com.care4elders.event.entity.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReminderRepository extends MongoRepository<Reminder, String> {
    List<Reminder> findByEventId(String eventId);
}