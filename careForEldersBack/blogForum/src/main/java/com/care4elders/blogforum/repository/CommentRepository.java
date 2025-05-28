package com.care4elders.blogforum.repository;

import com.care4elders.blogforum.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // Find comments by post ID
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(String postId);
    
    // Find comments by post ID (without ordering)
    List<Comment> findByPostIdAndIsDeletedFalse(String postId);
    
    // Find top-level comments by post ID
    List<Comment> findByPostIdAndParentCommentIdIsNullAndIsDeletedFalseOrderByCreatedAtDesc(String postId);
    
    // Find replies by parent comment ID
    List<Comment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(String parentCommentId);
    
    // Find comments by user ID
    List<Comment> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId);
    
    // Search comments by content
    List<Comment> findByContentContainingIgnoreCaseAndIsDeletedFalse(String content);
    
    // Find all non-deleted comments
    List<Comment> findByIsDeletedFalse();
    
    // Find comments by sentiment
    List<Comment> findBySentimentAndIsDeletedFalse(String sentiment);
    
    // Find comments by sentiment score range
    @Query("{ 'sentimentScore': { $gte: ?0, $lte: ?1 }, 'isDeleted': false }")
    List<Comment> findBySentimentScoreRangeAndIsDeletedFalse(double minScore, double maxScore);
    
    // Find comments by emotional tone
    List<Comment> findByEmotionalToneAndIsDeletedFalse(String emotionalTone);
    
    // Find comments by post ID and sentiment
    List<Comment> findByPostIdAndSentimentAndIsDeletedFalseOrderByCreatedAtDesc(String postId, String sentiment);
    
    // Find comments by keywords
    @Query("{ 'keywords': { $in: [?0] }, 'isDeleted': false }")
    List<Comment> findByKeywordsContainingAndIsDeletedFalse(String keyword);
    
    // Find comments by named entities
    @Query("{ 'namedEntities': { $regex: ?0, $options: 'i' }, 'isDeleted': false }")
    List<Comment> findByNamedEntitiesContainingAndIsDeletedFalse(String entity);
    
    // Find comments without sentiment analysis (for batch processing)
    List<Comment> findBySentimentIsNull();
    
    // Find comments without sentiment analysis and not deleted
    List<Comment> findBySentimentIsNullAndIsDeletedFalse();
    
    // Find comments by post ID and emotional tone
    List<Comment> findByPostIdAndEmotionalToneAndIsDeletedFalseOrderByCreatedAtDesc(String postId, String emotionalTone);
    
    // Find comments created after a specific date
    List<Comment> findByCreatedAtAfterAndIsDeletedFalse(LocalDateTime date);
    
    // Find comments with high sentiment scores (positive)
    @Query("{ 'sentimentScore': { $gte: 0.5 }, 'isDeleted': false }")
    List<Comment> findPositiveCommentsAndIsDeletedFalse();
    
    // Find comments with low sentiment scores (negative)
    @Query("{ 'sentimentScore': { $lte: -0.5 }, 'isDeleted': false }")
    List<Comment> findNegativeCommentsAndIsDeletedFalse();
    
    // Find comments with neutral sentiment scores
    @Query("{ 'sentimentScore': { $gte: -0.3, $lte: 0.3 }, 'isDeleted': false }")
    List<Comment> findNeutralCommentsAndIsDeletedFalse();
    
    // Count comments by sentiment for a post
    @Query(value = "{ 'postId': ?0, 'sentiment': ?1, 'isDeleted': false }", count = true)
    long countByPostIdAndSentimentAndIsDeletedFalse(String postId, String sentiment);
    
    // Count comments by emotional tone for a post
    @Query(value = "{ 'postId': ?0, 'emotionalTone': ?1, 'isDeleted': false }", count = true)
    long countByPostIdAndEmotionalToneAndIsDeletedFalse(String postId, String emotionalTone);
    
    // Find comments with specific word count range
    @Query("{ 'wordCount': { $gte: ?0, $lte: ?1 }, 'isDeleted': false }")
    List<Comment> findByWordCountRangeAndIsDeletedFalse(int minWords, int maxWords);
    
    // Find recent comments for analysis (last 24 hours)
    @Query("{ 'createdAt': { $gte: ?0 }, 'isDeleted': false }")
    List<Comment> findRecentCommentsAndIsDeletedFalse(LocalDateTime since);
}