// SentimentRequest.java
package com.care4elders.blogforum.dto;

public class SentimentRequest {
    private String text;

    public SentimentRequest() {}

    public SentimentRequest(String text) {
        this.text = text;
    }

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}