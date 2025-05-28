package com.care4elders.blogforum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.care4elders.blogforum.dto.PostImageRequest;
import com.care4elders.blogforum.dto.PostRequest;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.model.LikePost;
import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.model.PostImage;
import com.care4elders.blogforum.repository.PostRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileUploadService fileUploadService;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
    }

    @Override
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByPublishedTrueAndIsDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Post getPostById(String id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    @Override
    public Post createPost(PostRequest postRequest, String authorId, String authorName) {
        log.info("Creating new post for author: {} with title: {}", authorName, postRequest.getTitle());
        
        Post.PostBuilder postBuilder = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .tags(postRequest.getTags() != null ? postRequest.getTags() : new ArrayList<>());
        
        // Handle featured image
        if (postRequest.getFeaturedImageUrl() != null && !postRequest.getFeaturedImageUrl().isEmpty()) {
            log.info("Adding featured image to post: {}", postRequest.getFeaturedImageName());
            postBuilder
                    .featuredImageUrl(postRequest.getFeaturedImageUrl())
                    .featuredImageName(postRequest.getFeaturedImageName())
                    .featuredImageType(postRequest.getFeaturedImageType())
                    .featuredImageSize(postRequest.getFeaturedImageSize())
                    .imageAltText(postRequest.getImageAltText())
                    .imageCaption(postRequest.getImageCaption());
        }
        
        // Handle additional images
        if (postRequest.getAdditionalImages() != null && !postRequest.getAdditionalImages().isEmpty()) {
            log.info("Adding {} additional images to post", postRequest.getAdditionalImages().size());
            List<PostImage> additionalImages = new ArrayList<>();
            
            for (PostImageRequest imageRequest : postRequest.getAdditionalImages()) {
                PostImage postImage = PostImage.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .url(imageRequest.getUrl())
                        .fileName(imageRequest.getFileName())
                        .fileType(imageRequest.getFileType())
                        .fileSize(imageRequest.getFileSize())
                        .altText(imageRequest.getAltText())
                        .caption(imageRequest.getCaption())
                        .order(imageRequest.getOrder())
                        .uploadedAt(LocalDateTime.now())
                        .build();
                additionalImages.add(postImage);
            }
            postBuilder.additionalImages(additionalImages);
        }
        
        Post post = postBuilder.build();
        Post savedPost = postRepository.save(post);
        
        log.info("Successfully created post with ID: {}", savedPost.getId());
        return savedPost;
    }

    @Override
    public Post updatePost(String id, PostRequest postRequest) throws PostNotFoundException {
        log.info("Updating post with ID: {}", id);
        
        Post post = getPostById(id);
        
        // Update basic fields
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setTags(postRequest.getTags() != null ? postRequest.getTags() : new ArrayList<>());
        post.setUpdatedAt(LocalDateTime.now());
        
        // Update featured image if provided
        if (postRequest.getFeaturedImageUrl() != null && !postRequest.getFeaturedImageUrl().isEmpty()) {
            log.info("Updating featured image for post: {}", id);
            
            // If there was a previous featured image, we might want to delete it
            if (post.getFeaturedImageUrl() != null && !post.getFeaturedImageUrl().equals(postRequest.getFeaturedImageUrl())) {
                try {
                    // Extract file path from URL and delete old image
                    String oldImagePath = extractFilePathFromUrl(post.getFeaturedImageUrl());
                    if (oldImagePath != null) {
                        fileUploadService.deleteImage(oldImagePath);
                        log.info("Deleted old featured image: {}", oldImagePath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete old featured image: {}", e.getMessage());
                }
            }
            
            post.setFeaturedImageUrl(postRequest.getFeaturedImageUrl());
            post.setFeaturedImageName(postRequest.getFeaturedImageName());
            post.setFeaturedImageType(postRequest.getFeaturedImageType());
            post.setFeaturedImageSize(postRequest.getFeaturedImageSize());
            post.setImageAltText(postRequest.getImageAltText());
            post.setImageCaption(postRequest.getImageCaption());
        }
        
        // Update additional images if provided
        if (postRequest.getAdditionalImages() != null) {
            log.info("Updating additional images for post: {}", id);
            
            // Delete old additional images
            if (post.getAdditionalImages() != null && !post.getAdditionalImages().isEmpty()) {
                for (PostImage oldImage : post.getAdditionalImages()) {
                    try {
                        String oldImagePath = extractFilePathFromUrl(oldImage.getUrl());
                        if (oldImagePath != null) {
                            fileUploadService.deleteImage(oldImagePath);
                            log.info("Deleted old additional image: {}", oldImagePath);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to delete old additional image: {}", e.getMessage());
                    }
                }
            }
            
            // Add new additional images
            List<PostImage> additionalImages = new ArrayList<>();
            for (PostImageRequest imageRequest : postRequest.getAdditionalImages()) {
                PostImage postImage = PostImage.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .url(imageRequest.getUrl())
                        .fileName(imageRequest.getFileName())
                        .fileType(imageRequest.getFileType())
                        .fileSize(imageRequest.getFileSize())
                        .altText(imageRequest.getAltText())
                        .caption(imageRequest.getCaption())
                        .order(imageRequest.getOrder())
                        .uploadedAt(LocalDateTime.now())
                        .build();
                additionalImages.add(postImage);
            }
            post.setAdditionalImages(additionalImages);
        }
        
        Post updatedPost = postRepository.save(post);
        log.info("Successfully updated post with ID: {}", id);
        return updatedPost;
    }

    @Override
    public void deletePost(String id) throws PostNotFoundException {
        log.info("Deleting post with ID: {}", id);
        
        Post post = getPostById(id);
        
        // Delete associated images before soft deleting the post
        deletePostImages(post);
        
        // Soft delete
        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        
        log.info("Successfully deleted post with ID: {}", id);
    }

    @Override
    public void removeImageFromPost(String postId, String imageId) throws PostNotFoundException {
        log.info("Removing image {} from post {}", imageId, postId);
        
        Post post = getPostById(postId);
        boolean imageRemoved = false;
        
        // Check if it's the featured image
        if (post.getFeaturedImageName() != null && 
            (post.getFeaturedImageName().contains(imageId) || imageId.equals("featured"))) {
            
            try {
                String imagePath = extractFilePathFromUrl(post.getFeaturedImageUrl());
                if (imagePath != null) {
                    fileUploadService.deleteImage(imagePath);
                    log.info("Deleted featured image file: {}", imagePath);
                }
            } catch (Exception e) {
                log.warn("Failed to delete featured image file: {}", e.getMessage());
            }
            
            post.setFeaturedImageUrl(null);
            post.setFeaturedImageName(null);
            post.setFeaturedImageType(null);
            post.setFeaturedImageSize(null);
            post.setImageAltText(null);
            post.setImageCaption(null);
            imageRemoved = true;
        }
        
        // Check additional images
        if (post.getAdditionalImages() != null && !post.getAdditionalImages().isEmpty()) {
            Optional<PostImage> imageToRemove = post.getAdditionalImages().stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst();
            
            if (imageToRemove.isPresent()) {
                try {
                    String imagePath = extractFilePathFromUrl(imageToRemove.get().getUrl());
                    if (imagePath != null) {
                        fileUploadService.deleteImage(imagePath);
                        log.info("Deleted additional image file: {}", imagePath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete additional image file: {}", e.getMessage());
                }
                
                post.getAdditionalImages().removeIf(image -> image.getId().equals(imageId));
                imageRemoved = true;
            }
        }
        
        if (imageRemoved) {
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
            log.info("Successfully removed image {} from post {}", imageId, postId);
        } else {
            log.warn("Image {} not found in post {}", imageId, postId);
            throw new IllegalArgumentException("Image not found in post");
        }
    }

    @Override
    public List<Post> searchPostsByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(title);
    }

    @Override
    public List<Post> searchPostsByContent(String content) {
        return postRepository.findByContentContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(content);
    }

    @Override
    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTagsContainingAndPublishedTrueAndIsDeletedFalse(tag);
    }

    @Override
    public Post incrementViewCount(String id) throws PostNotFoundException {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    @Override
    public LikePost likePost(String id, String userId) throws PostNotFoundException {
        Post post = getPostById(id);
        
        // Check if user already liked the post
        Optional<LikePost> existingLike = post.getLikes().stream()
                .filter(like -> like.getUserId().equals(userId))
                .findFirst();
        
        if (existingLike.isPresent()) {
            return existingLike.get();
        }
        
        // Add new like
        LikePost like = LikePost.builder()
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        
        post.getLikes().add(like);
        postRepository.save(post);
        
        log.info("User {} liked post {}", userId, id);
        return like;
    }

    @Override
    public void removeLike(String id, String userId) throws PostNotFoundException {
        Post post = getPostById(id);
        
        int initialSize = post.getLikes().size();
        post.setLikes(
                post.getLikes().stream()
                        .filter(like -> !like.getUserId().equals(userId))
                        .toList()
        );
        
        if (post.getLikes().size() < initialSize) {
            postRepository.save(post);
            log.info("User {} unliked post {}", userId, id);
        }
    }

    @Override
    public int getLikesCount(Post post) {
        return post.getLikes() != null ? post.getLikes().size() : 0;
    }

    @Override
    public Post getMostLikedPost() {
        List<Post> posts = postRepository.findByPublishedTrueAndIsDeletedFalse();
        
        return posts.stream()
                .max(Comparator.comparingInt(post -> getLikesCount(post)))
                .orElse(null);
    }

    @Override
    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    public void updateCommentsCount(String postId, long commentsCount) {
        try {
            Post post = getPostById(postId);
            post.setCommentsCount(commentsCount);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
            log.info("Updated comments count for post {} to {}", postId, commentsCount);
        } catch (PostNotFoundException e) {
            log.error("Could not update comments count for post: {}", postId, e);
        }
    }

    // Helper methods for image management
    
    /**
     * Delete all images associated with a post
     */
    private void deletePostImages(Post post) {
        // Delete featured image
        if (post.getFeaturedImageUrl() != null) {
            try {
                String imagePath = extractFilePathFromUrl(post.getFeaturedImageUrl());
                if (imagePath != null) {
                    fileUploadService.deleteImage(imagePath);
                    log.info("Deleted featured image: {}", imagePath);
                }
            } catch (Exception e) {
                log.warn("Failed to delete featured image: {}", e.getMessage());
            }
        }
        
        // Delete additional images
        if (post.getAdditionalImages() != null && !post.getAdditionalImages().isEmpty()) {
            for (PostImage image : post.getAdditionalImages()) {
                try {
                    String imagePath = extractFilePathFromUrl(image.getUrl());
                    if (imagePath != null) {
                        fileUploadService.deleteImage(imagePath);
                        log.info("Deleted additional image: {}", imagePath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete additional image: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Extract file path from URL for deletion
     * Converts URL like "/uploads/2024/01/15/filename.jpg" to actual file path
     */
    private String extractFilePathFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // Remove leading slash and convert to file path
        if (url.startsWith("/uploads/")) {
            return "uploads/" + url.substring("/uploads/".length());
        }
        
        return null;
    }
    
    /**
     * Get total size of all images in a post
     */
    public long getTotalImageSize(Post post) {
        long totalSize = 0;
        
        if (post.getFeaturedImageSize() != null) {
            totalSize += post.getFeaturedImageSize();
        }
        
        if (post.getAdditionalImages() != null) {
            totalSize += post.getAdditionalImages().stream()
                    .mapToLong(image -> image.getFileSize() != null ? image.getFileSize() : 0)
                    .sum();
        }
        
        return totalSize;
    }
    
    /**
     * Get total number of images in a post
     */
    public int getTotalImageCount(Post post) {
        int count = 0;
        
        if (post.getFeaturedImageUrl() != null) {
            count++;
        }
        
        if (post.getAdditionalImages() != null) {
            count += post.getAdditionalImages().size();
        }
        
        return count;
    }
}