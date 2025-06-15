package com.care4elders.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reminders")
@Data
public class Reminder {
    @Id
    private String id;
    private String message;
    private Date reminderDate;
    private String eventId;
}