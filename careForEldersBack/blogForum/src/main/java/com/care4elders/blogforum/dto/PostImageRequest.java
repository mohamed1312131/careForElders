package com.care4elders.blogforum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageRequest {
    private String url;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String altText;
    private String caption;
    private int order;
}