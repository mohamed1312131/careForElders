package com.care4elders.blogforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.care4elders.blogforum.model.LikeComment;

@Repository
public interface LikeCommentRepository extends MongoRepository<LikeComment, String> {
    
    List<LikeComment> findByCommentId(String commentId);
    
    List<LikeComment> findByUserId(String userId);
    
    Optional<LikeComment> findByCommentIdAndUserId(String commentId, String userId);
    
    long countByCommentIdAndIsLikeTrue(String commentId);
    
    void deleteByCommentIdAndUserId(String commentId, String userId);
}