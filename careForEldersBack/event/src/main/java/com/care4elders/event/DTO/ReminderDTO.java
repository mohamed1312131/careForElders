package com.care4elders.event.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ReminderDTO {
    private String message;
    private Date reminderDate;
    private String eventId;
}