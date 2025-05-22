package com.care4elders.blogforum.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.care4elders.blogforum.dto.CommentRequest;
import com.care4elders.blogforum.exception.CommentNotFoundException;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.model.LikeComment;
import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.repository.CommentRepository;
import com.care4elders.blogforum.repository.PostRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    
    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostIdAndParentCommentIdIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId);
    }
    
    public List<Comment> getCommentsByUser(String userId) {
        return commentRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }
    
    public List<Comment> getRepliesByParentCommentId(String parentCommentId) {
        return commentRepository.findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentCommentId);
    }
    
    public Comment getCommentById(String id) throws CommentNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + id));
    }
    
    public Comment createComment(String postId, CommentRequest commentRequest, String authorId, String authorName) 
            throws PostNotFoundException {
        
        // Verify post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        
        Comment comment = Comment.builder()
                .postId(postId)
                .content(commentRequest.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .parentCommentId(commentRequest.getParentCommentId())
                .build();
        
        return commentRepository.save(comment);
    }
    
    public Comment updateComment(String id, CommentRequest commentRequest) throws CommentNotFoundException {
        Comment comment = getCommentById(id);
        
        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    
    public void deleteComment(String id) throws CommentNotFoundException {
        Comment comment = getCommentById(id);
        
        // Soft delete
        comment.setDeleted(true);
        commentRepository.save(comment);
    }
    
    public LikeComment likeComment(String id, String userId) throws CommentNotFoundException {
        Comment comment = getCommentById(id);
        
        // Check if user already liked the comment
        boolean alreadyLiked = comment.getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(userId));
        
        if (!alreadyLiked) {
            LikeComment like = LikeComment.builder()
                    .userId(userId)
                    .build();
            
            comment.getLikes().add(like);
            commentRepository.save(comment);
            
            return like;
        }
        
        // Return the existing like
        return comment.getLikes().stream()
                .filter(like -> like.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    
    public void removeLike(String id, String userId) throws CommentNotFoundException {
        Comment comment = getCommentById(id);
        
        comment.setLikes(
                comment.getLikes().stream()
                        .filter(like -> !like.getUserId().equals(userId))
                        .toList()
        );
        
        commentRepository.save(comment);
    }
    
    public int getLikesCount(Comment comment) {
        return comment.getLikes().size();
    }
    
    public long getCommentsCountByPostId(String postId) {
        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }
}