package com.care4elders.event.DTO;

import lombok.Data;

@Data
public class EmailDTO {
    private String to;
    private String subject;
    private String text;

    public EmailDTO(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}