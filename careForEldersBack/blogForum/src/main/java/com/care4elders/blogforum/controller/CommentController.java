package com.care4elders.blogforum.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.care4elders.blogforum.dto.CommentRequest;
import com.care4elders.blogforum.exception.CommentNotFoundException;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.exception.ValidationErrorResponse;
import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.model.LikeComment;
import com.care4elders.blogforum.service.CommentService;
import com.care4elders.blogforum.util.ValidationUtils;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        try {
            Comment comment = commentService.getCommentById(id);
            return ResponseEntity.ok(comment);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Comment>> getRepliesByParentCommentId(@PathVariable String id) {
        List<Comment> replies = commentService.getRepliesByParentCommentId(id);
        return ResponseEntity.ok(replies);
    }
    
    @PostMapping
    public ResponseEntity<?> createComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest commentRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(ValidationUtils.createValidationErrorResponse(bindingResult));
        }
        
        // TODO: Get actual user ID and name from authentication
        String authorId = "user123";
        String authorName = "User";
        
        try {
            Comment createdComment = commentService.createComment(postId, commentRequest, authorId, authorName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest commentRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(ValidationUtils.createValidationErrorResponse(bindingResult));
        }
        
        try {
            Comment updatedComment = commentService.updateComment(id, commentRequest);
            return ResponseEntity.ok(updatedComment);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable String id) {
        // TODO: Get actual user ID from authentication
        String userId = "user123";
        
        try {
            LikeComment like = commentService.likeComment(id, userId);
            return ResponseEntity.ok(like);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> removeLike(@PathVariable String id) {
        // TODO: Get actual user ID from authentication
        String userId = "user123";
        
        try {
            commentService.removeLike(id, userId);
            return ResponseEntity.noContent().build();
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable String id) {
        try {
            Comment comment = commentService.getCommentById(id);
            int likesCount = commentService.getLikesCount(comment);
            return ResponseEntity.ok(likesCount);
        } catch (CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}