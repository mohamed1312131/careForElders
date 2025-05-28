package com.care4elders.blogforum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {
    private String id;
    private String url;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String altText;
    private String caption;
    private int order; // For ordering multiple images
    
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}