package com.care4elders.blogforum.config;

import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.repository.CommentRepository;
import com.care4elders.blogforum.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class NLPAnalysisConfig {

    private static final Logger logger = LoggerFactory.getLogger(NLPAnalysisConfig.class);
    
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    
    @Autowired
    public NLPAnalysisConfig(CommentRepository commentRepository, CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }
    
    // Process unanalyzed comments every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void processUnanalyzedComments() {
        logger.info("Starting scheduled NLP analysis of unanalyzed comments");
        
        // Find comments without sentiment analysis
        List<Comment> unanalyzedComments = commentRepository.findBySentimentIsNull();
        
        logger.info("Found {} unanalyzed comments", unanalyzedComments.size());
        
        for (Comment comment : unanalyzedComments) {
            try {
                commentService.analyzeAndSaveComment(comment);
                logger.debug("Successfully analyzed comment: {}", comment.getId());
            } catch (Exception e) {
                logger.error("Error analyzing comment {}: {}", comment.getId(), e.getMessage());
            }
        }
        
        logger.info("Completed scheduled NLP analysis");
    }
}