package com.care4elders.event.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventDTO {
    private String title;
    private Date date;
    private String location;
    private String description;
    private List<String> participantIds;
}