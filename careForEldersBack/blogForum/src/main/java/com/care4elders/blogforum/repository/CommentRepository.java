package com.care4elders.blogforum.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.care4elders.blogforum.model.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    List<Comment> findByPostIdAndParentCommentIdIsNullAndIsDeletedFalseOrderByCreatedAtDesc(String postId);
    
    List<Comment> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(String authorId);
    
    List<Comment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(String parentCommentId);
    
    long countByPostIdAndIsDeletedFalse(String postId);
}