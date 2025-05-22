package com.care4elders.blogforum.model;

import java.time.LocalDateTime;

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
@Document(collection = "likeComments")
public class LikeComment {
    
    @Id
    private String id;
    
    private String userId;
    private String commentId;
    
    @Builder.Default
    private boolean isLike = true;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}