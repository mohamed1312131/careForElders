package com.care4elders.blogforum.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "comments")
public class Comment {
    
    @Id
    private String id;
    private String content;
    private String postId;
    private String userId;
    private String username;
    private String parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted = false;
    
    // Enhanced Sentiment Analysis Fields
    private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL
    private Double sentimentScore; // -1.0 to 1.0
    private String emotionalTone; // ANGRY, SAD, EXCITED, WORRIED, GRATEFUL, etc.
    
    // Stanford CoreNLP Enhanced Fields
    private List<String> keywords = new ArrayList<>(); // Extracted keywords
    private List<String> namedEntities = new ArrayList<>(); // Named entities (format: "text:type")
    private Integer wordCount; // Number of words
    private Integer readingTimeSeconds; // Estimated reading time
    
    // Like system
    private Set<String> likedByUsers = new HashSet<>();
    private int likeCount = 0;
    
    // Reply system
    private List<Comment> replies = new ArrayList<>();
    
    // Constructors
    public Comment() {}
    
    public Comment(String content, String postId, String userId, String username) {
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getPostId() {
        return postId;
    }
    
    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getParentCommentId() {
        return parentCommentId;
    }
    
    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    // Enhanced Sentiment Analysis Getters and Setters
    public String getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    
    public Double getSentimentScore() {
        return sentimentScore;
    }
    
    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
    
    public String getEmotionalTone() {
        return emotionalTone;
    }
    
    public void setEmotionalTone(String emotionalTone) {
        this.emotionalTone = emotionalTone;
    }
    
    // Stanford CoreNLP Enhanced Fields Getters and Setters
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords != null ? keywords : new ArrayList<>();
    }
    
    public List<String> getNamedEntities() {
        return namedEntities;
    }
    
    public void setNamedEntities(List<String> namedEntities) {
        this.namedEntities = namedEntities != null ? namedEntities : new ArrayList<>();
    }
    
    public Integer getWordCount() {
        return wordCount;
    }
    
    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }
    
    public Integer getReadingTimeSeconds() {
        return readingTimeSeconds;
    }
    
    public void setReadingTimeSeconds(Integer readingTimeSeconds) {
        this.readingTimeSeconds = readingTimeSeconds;
    }
    
    // Like system methods
    public Set<String> getLikedByUsers() {
        return likedByUsers;
    }
    
    public void setLikedByUsers(Set<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
        this.likeCount = likedByUsers.size();
    }
    
    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public void addLike(String userId) {
        if (likedByUsers.add(userId)) {
            this.likeCount = likedByUsers.size();
        }
    }
    
    public void removeLike(String userId) {
        if (likedByUsers.remove(userId)) {
            this.likeCount = likedByUsers.size();
        }
    }
    
    public boolean isLikedBy(String userId) {
        return likedByUsers.contains(userId);
    }
    
    // Reply system methods
    public List<Comment> getReplies() {
        return replies;
    }
    
    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
    
    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
    }
    
    public void removeReply(Comment reply) {
        if (this.replies != null) {
            this.replies.remove(reply);
        }
    }
    
    public int getReplyCount() {
        return replies != null ? replies.size() : 0;
    }
    
    // Utility methods
    public boolean isReply() {
        return parentCommentId != null && !parentCommentId.isEmpty();
    }
    
    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }
    
    // Enhanced sentiment analysis utility methods
    public boolean isPositive() {
        return "POSITIVE".equalsIgnoreCase(sentiment);
    }
    
    public boolean isNegative() {
        return "NEGATIVE".equalsIgnoreCase(sentiment);
    }
    
    public boolean isNeutral() {
        return "NEUTRAL".equalsIgnoreCase(sentiment);
    }
    
    public String getSentimentEmoji() {
        if (sentiment == null) return "😐";
        
        switch (sentiment.toUpperCase()) {
            case "POSITIVE":
                return "😊";
            case "NEGATIVE":
                return "😞";
            case "NEUTRAL":
            default:
                return "😐";
        }
    }
    
    public String getEmotionalToneEmoji() {
        if (emotionalTone == null) return "";
        
        switch (emotionalTone.toUpperCase()) {
            case "ANGRY":
                return "😠";
            case "SAD":
                return "😢";
            case "EXCITED":
                return "🤩";
            case "WORRIED":
                return "😟";
            case "GRATEFUL":
                return "🙏";
            default:
                return "";
        }
    }
    
    // Enhanced utility methods
    public boolean hasKeywords() {
        return keywords != null && !keywords.isEmpty();
    }
    
    public boolean hasNamedEntities() {
        return namedEntities != null && !namedEntities.isEmpty();
    }
    
    public String getKeywordsAsString() {
        return keywords != null ? String.join(", ", keywords) : "";
    }
    
    public String getNamedEntitiesAsString() {
        return namedEntities != null ? String.join(", ", namedEntities) : "";
    }
    
    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", sentimentScore=" + sentimentScore +
                ", emotionalTone='" + emotionalTone + '\'' +
                ", keywords=" + keywords.size() +
                ", namedEntities=" + namedEntities.size() +
                ", wordCount=" + wordCount +
                ", likeCount=" + likeCount +
                ", createdAt=" + createdAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Comment comment = (Comment) o;
        return id != null ? id.equals(comment.id) : comment.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}