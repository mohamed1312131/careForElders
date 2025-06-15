package com.care4elders.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "events")
@Data
public class Event {
    @Id
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;
    private List<String> participantIds; // Stores user IDs from user-service
}