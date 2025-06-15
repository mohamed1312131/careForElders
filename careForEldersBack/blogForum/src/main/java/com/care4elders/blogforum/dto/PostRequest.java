package com.care4elders.blogforum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;
    
    private List<String> tags;
    
    // Image-related fields
    private String featuredImageUrl;
    private String featuredImageName;
    private String featuredImageType;
    private Long featuredImageSize;
    private String imageAltText;
    private String imageCaption;
    
    private List<PostImageRequest> additionalImages;
}