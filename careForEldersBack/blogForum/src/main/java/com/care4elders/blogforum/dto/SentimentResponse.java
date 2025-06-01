// SentimentResponse.java
package com.care4elders.blogforum.dto;

import java.util.List;

public class SentimentResponse {
    private List<Sentiment> sentiment;

    public SentimentResponse() {}

    // Getters and setters
    public List<Sentiment> getSentiment() { return sentiment; }
    public void setSentiment(List<Sentiment> sentiment) { this.sentiment = sentiment; }

    public static class Sentiment {
        private String label;
        private double score;

        public Sentiment() {}

        // Getters and setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
    }
}