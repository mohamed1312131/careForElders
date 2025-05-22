package com.care4elders.blogforum.controller;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.care4elders.blogforum.dto.PostRequest;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.exception.ValidationError;
import com.care4elders.blogforum.exception.ValidationErrorResponse;

import com.care4elders.blogforum.model.LikePost;
import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.service.PostService;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    
    private final PostService postService;

    
    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Post> posts = postService.getPublishedPosts(pageRequest);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        try {
            Post post = postService.getPostById(id);
            // Increment view count
            post = postService.incrementViewCount(id);
            return ResponseEntity.ok(post);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable String userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String tag) {
        
        if (title != null && !title.isEmpty()) {
            return ResponseEntity.ok(postService.searchPostsByTitle(title));
        } else if (content != null && !content.isEmpty()) {
            return ResponseEntity.ok(postService.searchPostsByContent(content));
        } else if (tag != null && !tag.isEmpty()) {
            return ResponseEntity.ok(postService.getPostsByTag(tag));
        } else {
            return ResponseEntity.ok(postService.getAllPosts());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest postRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(createValidationErrorResponse(bindingResult));
        }
        
        // TODO: Get actual user ID and name from authentication
        String authorId = "doctor123";
        String authorName = "Doctor";
        
        Post createdPost = postService.createPost(postRequest, authorId, authorName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostRequest postRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(createValidationErrorResponse(bindingResult));
        }
        
        try {
            Post updatedPost = postService.updatePost(id, postRequest);
            return ResponseEntity.ok(updatedPost);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
  
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id) {
        // TODO: Get actual user ID from authentication
        String userId = "user123";
        
        try {
            LikePost like = postService.likePost(id, userId);
            return ResponseEntity.ok(like);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    

    
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> removeLike(@PathVariable String id) {
        // TODO: Get actual user ID from authentication
        String userId = "user123";
        
        try {
            postService.removeLike(id, userId);
            return ResponseEntity.noContent().build();
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/most-liked")
    public ResponseEntity<Post> getMostLikedPost() {
        Post post = postService.getMostLikedPost();
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/{id}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable String id) {
        try {
            Post post = postService.getPostById(id);
            int likesCount = postService.getLikesCount(post);
            return ResponseEntity.ok(likesCount);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    private ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        List<ValidationError> errors = bindingResult.getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(), 
                        error.getDefaultMessage()))
                .toList();
                
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}