package com.care4elders.blogforum.service;

import com.care4elders.blogforum.model.Comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentService {
    Comment saveComment(Comment comment);
    Comment analyzeAndSaveComment(Comment comment);
    Optional<Comment> findCommentById(String id);
    List<Comment> findAllCommentsByPostId(String postId);
    List<Comment> findTopLevelCommentsByPostId(String postId);
    List<Comment> findRepliesByCommentId(String commentId);
    List<Comment> findCommentsByUserId(String userId);
    void deleteComment(String id);
    Comment addReplyToComment(String parentId, Comment reply);
    Comment likeComment(String id, String userId);
    Comment unlikeComment(String id, String userId);
    List<Comment> searchCommentsByContent(String query);
    List<Comment> findMostLikedCommentsByPostId(String postId, int limit);
    List<Comment> findMostLikedComments(int limit);
    List<Comment> findCommentsBySentiment(String sentiment);
    
    // Updated to use StanfordNLP service result type
    StanfordNLPSentimentAnalysisService.SentimentResult analyzeSentiment(String text);
    
    // New methods for NLP analysis
    StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis analyzeComprehensive(String text);
    List<StanfordNLPSentimentAnalysisService.NamedEntity> extractEntities(String text);
    List<String> extractKeywords(String text);
    List<Comment> findCommentsByPostAndSentiment(String postId, String sentiment);
    List<Comment> findCommentsByEmotionalTone(String emotionalTone);
    List<Comment> findCommentsByKeyword(String keyword);
    List<Comment> findCommentsByEntity(String entity);
    List<Comment> findCommentsBySentimentRange(double minScore, double maxScore);
    Map<String, Object> getNLPStatisticsForPost(String postId);
    int batchAnalyzeUnprocessedComments();
}