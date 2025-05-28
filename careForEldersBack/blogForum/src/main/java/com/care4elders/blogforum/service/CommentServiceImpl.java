package com.care4elders.blogforum.service;

import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    private final CommentRepository commentRepository;
    private final StanfordNLPSentimentAnalysisService nlpService;
    
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, 
                            StanfordNLPSentimentAnalysisService nlpService) {
        this.commentRepository = commentRepository;
        this.nlpService = nlpService;
    }
    
    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    @Override
    public Comment analyzeAndSaveComment(Comment comment) {
        logger.info("Analyzing comment with Stanford CoreNLP: {}", comment.getId());
        
        try {
            // Perform comprehensive analysis using Stanford NLP
            StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis analysis = 
                nlpService.performComprehensiveAnalysis(comment.getContent());
            
            if (analysis.getSentimentResult() != null) {
                // Set sentiment data
                comment.setSentiment(mapToSimpleSentiment(analysis.getSentimentResult().getSentiment()));
                comment.setSentimentScore(analysis.getSentimentResult().getScore());
                comment.setEmotionalTone(analysis.getEmotionalTone());
                
                // Set additional NLP data
                if (analysis.getKeywords() != null) {
                    comment.setKeywords(analysis.getKeywords());
                }
                
                if (analysis.getNamedEntities() != null) {
                    comment.setNamedEntities(analysis.getNamedEntities().stream()
                        .map(entity -> entity.getText() + ":" + entity.getType())
                        .collect(Collectors.toList()));
                }
                
                comment.setReadingTimeSeconds(analysis.getReadingTimeMinutes() * 60);
                comment.setWordCount(analysis.getWordCount());
                
                logger.info("Stanford CoreNLP analysis complete - Sentiment: {}, Score: {}, Entities: {}, Keywords: {}", 
                           comment.getSentiment(), comment.getSentimentScore(), 
                           comment.getNamedEntities() != null ? comment.getNamedEntities().size() : 0, 
                           comment.getKeywords() != null ? comment.getKeywords().size() : 0);
            }
            
        } catch (Exception e) {
            logger.error("Stanford CoreNLP analysis failed", e);
            // Set default values in case of error
            comment.setSentiment("NEUTRAL");
            comment.setSentimentScore(0.0);
            comment.setEmotionalTone("NEUTRAL");
            comment.setKeywords(new ArrayList<>());
            comment.setNamedEntities(new ArrayList<>());
        }
        
        return saveComment(comment);
    }
    
    @Override
    public Optional<Comment> findCommentById(String id) {
        return commentRepository.findById(id);
    }
    
    @Override
    public List<Comment> findAllCommentsByPostId(String postId) {
        return commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(postId);
    }
    
    @Override
    public List<Comment> findTopLevelCommentsByPostId(String postId) {
        return commentRepository.findByPostIdAndParentCommentIdIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId);
    }
    
    @Override
    public List<Comment> findRepliesByCommentId(String commentId) {
        return commentRepository.findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(commentId);
    }
    
    @Override
    public List<Comment> findCommentsByUserId(String userId) {
        return commentRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public void deleteComment(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            Comment commentToDelete = comment.get();
            commentToDelete.setDeleted(true);
            commentRepository.save(commentToDelete);
        }
    }
    
    @Override
    public Comment addReplyToComment(String parentId, Comment reply) {
        Optional<Comment> parentComment = commentRepository.findById(parentId);
        if (parentComment.isPresent()) {
            Comment parent = parentComment.get();
            if (parent.getReplies() == null) {
                parent.setReplies(new java.util.ArrayList<>());
            }
            parent.addReply(reply);
            return commentRepository.save(parent);
        }
        return null;
    }
    
    @Override
    public Comment likeComment(String id, String userId) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            Comment commentToLike = comment.get();
            commentToLike.addLike(userId);
            return commentRepository.save(commentToLike);
        }
        return null;
    }
    
    @Override
    public Comment unlikeComment(String id, String userId) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            Comment commentToUnlike = comment.get();
            commentToUnlike.removeLike(userId);
            return commentRepository.save(commentToUnlike);
        }
        return null;
    }
    
    @Override
    public List<Comment> searchCommentsByContent(String query) {
        return commentRepository.findByContentContainingIgnoreCaseAndIsDeletedFalse(query);
    }
    
    @Override
    public List<Comment> findMostLikedCommentsByPostId(String postId, int limit) {
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
        return comments.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Comment> findMostLikedComments(int limit) {
        List<Comment> comments = commentRepository.findByIsDeletedFalse();
        return comments.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Comment> findCommentsBySentiment(String sentiment) {
        return commentRepository.findBySentimentAndIsDeletedFalse(sentiment);
    }
    
    @Override
    public StanfordNLPSentimentAnalysisService.SentimentResult analyzeSentiment(String text) {
        try {
            return nlpService.analyzeSentiment(text);
        } catch (Exception e) {
            logger.error("Error analyzing sentiment", e);
            // Return a default result in case of error
            return new StanfordNLPSentimentAnalysisService.SentimentResult(
                "NEUTRAL", 
                0.0, 
                "Analysis failed: " + e.getMessage(), 
                new ArrayList<>()
            );
        }
    }
    
    @Override
    public StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis analyzeComprehensive(String text) {
        try {
            return nlpService.performComprehensiveAnalysis(text);
        } catch (Exception e) {
            logger.error("Error performing comprehensive analysis", e);
            return new StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis();
        }
    }
    
    @Override
    public List<StanfordNLPSentimentAnalysisService.NamedEntity> extractEntities(String text) {
        try {
            return nlpService.extractNamedEntities(text);
        } catch (Exception e) {
            logger.error("Error extracting named entities", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> extractKeywords(String text) {
        try {
            return nlpService.extractKeywords(text);
        } catch (Exception e) {
            logger.error("Error extracting keywords", e);
            return new ArrayList<>();
        }
    }
    
    // Helper method to map Stanford sentiment to simple sentiment
    private String mapToSimpleSentiment(String stanfordSentiment) {
        if (stanfordSentiment == null) return "NEUTRAL";
        
        switch (stanfordSentiment.toUpperCase()) {
            case "VERY_POSITIVE":
            case "POSITIVE":
                return "POSITIVE";
            case "VERY_NEGATIVE":
            case "NEGATIVE":
                return "NEGATIVE";
            case "NEUTRAL":
            default:
                return "NEUTRAL";
        }
    }
    // Add these methods to your CommentServiceImpl

@Override
public List<Comment> findCommentsByPostAndSentiment(String postId, String sentiment) {
    return commentRepository.findByPostIdAndSentimentAndIsDeletedFalseOrderByCreatedAtDesc(postId, sentiment);
}

@Override
public List<Comment> findCommentsByEmotionalTone(String emotionalTone) {
    return commentRepository.findByEmotionalToneAndIsDeletedFalse(emotionalTone);
}

@Override
public List<Comment> findCommentsByKeyword(String keyword) {
    return commentRepository.findByKeywordsContainingAndIsDeletedFalse(keyword);
}

@Override
public List<Comment> findCommentsByEntity(String entity) {
    return commentRepository.findByNamedEntitiesContainingAndIsDeletedFalse(entity);
}

@Override
public List<Comment> findCommentsBySentimentRange(double minScore, double maxScore) {
    return commentRepository.findBySentimentScoreRangeAndIsDeletedFalse(minScore, maxScore);
}

@Override
public Map<String, Object> getNLPStatisticsForPost(String postId) {
    List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
    
    long positiveCount = comments.stream().filter(c -> "POSITIVE".equals(c.getSentiment())).count();
    long negativeCount = comments.stream().filter(c -> "NEGATIVE".equals(c.getSentiment())).count();
    long neutralCount = comments.stream().filter(c -> "NEUTRAL".equals(c.getSentiment())).count();
    
    double avgSentimentScore = comments.stream()
        .filter(c -> c.getSentimentScore() != null)
        .mapToDouble(Comment::getSentimentScore)
        .average()
        .orElse(0.0);
    
    Map<String, Long> emotionCounts = comments.stream()
        .filter(c -> c.getEmotionalTone() != null)
        .collect(Collectors.groupingBy(Comment::getEmotionalTone, Collectors.counting()));
    
    return Map.of(
        "totalComments", comments.size(),
        "sentimentDistribution", Map.of(
            "positive", positiveCount,
            "negative", negativeCount,
            "neutral", neutralCount
        ),
        "averageSentimentScore", avgSentimentScore,
        "emotionDistribution", emotionCounts
    );
}

@Override
public int batchAnalyzeUnprocessedComments() {
    List<Comment> unprocessedComments = commentRepository.findBySentimentIsNullAndIsDeletedFalse();
    
    for (Comment comment : unprocessedComments) {
        try {
            analyzeAndSaveComment(comment);
        } catch (Exception e) {
            logger.error("Failed to analyze comment {}: {}", comment.getId(), e.getMessage());
        }
    }
    
    return unprocessedComments.size();
}
}