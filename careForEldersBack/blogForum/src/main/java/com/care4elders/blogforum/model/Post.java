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
@Document(collection = "posts")
public class Post {
    
    @Id
    private String id;
    
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Builder.Default
    private List<LikePost> likes = new ArrayList<>();
    
    @Builder.Default
    private int viewCount = 0;
    
    @Builder.Default
    private long commentsCount = 0;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Builder.Default
    private boolean published = true;
    
    @Builder.Default
    private boolean isDeleted = false;
    // New image-related fields
    private String featuredImageUrl;
    private String featuredImageName;
    private String featuredImageType;
    private Long featuredImageSize;
    
    @Builder.Default
    private List<PostImage> additionalImages = new ArrayList<>();
    
    // Image metadata
    private String imageAltText;
    private String imageCaption;
}