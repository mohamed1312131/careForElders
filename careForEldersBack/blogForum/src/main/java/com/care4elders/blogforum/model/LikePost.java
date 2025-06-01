package com.care4elders.blogforum.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikePost {
    private String id;
    private String postId;
    private String userId;
    private boolean isLike; // true for like, false for dislike
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}