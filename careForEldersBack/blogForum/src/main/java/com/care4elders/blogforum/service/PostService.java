package com.care4elders.blogforum.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4elders.blogforum.dto.PostRequest;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.model.LikePost;
import com.care4elders.blogforum.model.Post;

public interface PostService {
    // Existing methods
    List<Post> getAllPosts();
    Page<Post> getPublishedPosts(Pageable pageable);
    Post getPostById(String id) throws PostNotFoundException;
    Post createPost(PostRequest postRequest, String authorId, String authorName);
    Post updatePost(String id, PostRequest postRequest) throws PostNotFoundException;
    void deletePost(String id) throws PostNotFoundException;
    List<Post> searchPostsByTitle(String title);
    List<Post> searchPostsByContent(String content);
    List<Post> getPostsByTag(String tag);
    Post incrementViewCount(String id) throws PostNotFoundException;
    
    // Like-related methods
    LikePost likePost(String id, String userId) throws PostNotFoundException;
    void removeLike(String id, String userId) throws PostNotFoundException;
    int getLikesCount(Post post);
    Post getMostLikedPost();
    
    // User-related methods
    List<Post> getPostsByUser(String userId);
    
    // Comment-related methods
    void updateCommentsCount(String postId, long commentsCount);
    
    // New image-related methods
    void removeImageFromPost(String postId, String imageId) throws PostNotFoundException;
}