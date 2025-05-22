package com.care4elders.blogforum.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    
    @Id
    private String id;
    
    private String postId;
    private String content;
    private String authorId;
    private String authorName;
    
    @Builder.Default
    private List<LikeComment> likes = new ArrayList<>();
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Builder.Default
    private boolean isDeleted = false;
    
    // For nested comments/replies
    private String parentCommentId;
    
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
}