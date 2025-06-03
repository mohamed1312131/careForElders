package com.care4elders.blogforum.controller;

import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.service.CommentService;
import com.care4elders.blogforum.service.StanfordNLPSentimentAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    private final CommentService commentService;
    private final StanfordNLPSentimentAnalysisService nlpService;
    
    @Autowired
    public CommentController(CommentService commentService,
                           StanfordNLPSentimentAnalysisService nlpService) {
        this.commentService = commentService;
        this.nlpService = nlpService;
    }
    
    /**
     * Create a new comment with automatic Stanford CoreNLP analysis
     */
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        logger.info("Creating new comment for post: {}", comment.getPostId());
        
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentService.analyzeAndSaveComment(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }
    
    /**
     * Create a comment without sentiment analysis (if needed)
     */
    @PostMapping("/basic")
    public ResponseEntity<Comment> createBasicComment(@RequestBody Comment comment) {
        logger.info("Creating basic comment for post: {}", comment.getPostId());
        
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentService.saveComment(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }
    
    /**
     * Analyze sentiment of existing comment using Stanford CoreNLP
     */
    @PostMapping("/{id}/analyze")
    public ResponseEntity<Comment> analyzeSentiment(@PathVariable String id) {
        logger.info("Analyzing sentiment for comment: {}", id);
        
        Optional<Comment> existingComment = commentService.findCommentById(id);
        if (existingComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Comment comment = existingComment.get();
        Comment analyzedComment = commentService.analyzeAndSaveComment(comment);
        return ResponseEntity.ok(analyzedComment);
    }
    
    /**
     * Get comprehensive analysis for text using Stanford CoreNLP
     */
    @PostMapping("/analyze-comprehensive")
    public ResponseEntity<StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis> analyzeComprehensive(
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Performing comprehensive analysis");
        StanfordNLPSentimentAnalysisService.ComprehensiveAnalysis result = 
            commentService.analyzeComprehensive(text);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get sentiment analysis for text (without saving)
     */
    @PostMapping("/analyze-text")
    public ResponseEntity<StanfordNLPSentimentAnalysisService.SentimentResult> analyzeText(
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Analyzing sentiment for text");
        StanfordNLPSentimentAnalysisService.SentimentResult result = commentService.analyzeSentiment(text);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Extract named entities from text
     */
    @PostMapping("/extract-entities")
    public ResponseEntity<List<StanfordNLPSentimentAnalysisService.NamedEntity>> extractEntities(
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Extracting named entities");
        List<StanfordNLPSentimentAnalysisService.NamedEntity> entities = 
            commentService.extractEntities(text);
        return ResponseEntity.ok(entities);
    }
    
    /**
     * Extract keywords from text
     */
    @PostMapping("/extract-keywords")
    public ResponseEntity<List<String>> extractKeywords(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Extracting keywords");
        List<String> keywords = commentService.extractKeywords(text);
        return ResponseEntity.ok(keywords);
    }
    
    /**
     * Get all comments for a post
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postId) {
        logger.info("Fetching comments for post: {}", postId);
        List<Comment> comments = commentService.findAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get comments by sentiment
     */
    @GetMapping("/sentiment/{sentiment}")
    public ResponseEntity<List<Comment>> getCommentsBySentiment(@PathVariable String sentiment) {
        logger.info("Fetching comments with sentiment: {}", sentiment);
        List<Comment> comments = commentService.findCommentsBySentiment(sentiment.toUpperCase());
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get comments by sentiment for a specific post
     */
    @GetMapping("/post/{postId}/sentiment/{sentiment}")
    public ResponseEntity<List<Comment>> getCommentsByPostAndSentiment(
            @PathVariable String postId, 
            @PathVariable String sentiment) {
        logger.info("Fetching comments for post {} with sentiment: {}", postId, sentiment);
        List<Comment> comments = commentService.findCommentsByPostAndSentiment(postId, sentiment);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get comments by emotional tone
     */
    @GetMapping("/emotion/{emotionalTone}")
    public ResponseEntity<List<Comment>> getCommentsByEmotionalTone(@PathVariable String emotionalTone) {
        logger.info("Fetching comments with emotional tone: {}", emotionalTone);
        List<Comment> comments = commentService.findCommentsByEmotionalTone(emotionalTone.toUpperCase());
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Search comments by keywords
     */
    @GetMapping("/search-keywords")
    public ResponseEntity<List<Comment>> searchByKeywords(@RequestParam String keyword) {
        logger.info("Searching comments by keyword: {}", keyword);
        List<Comment> comments = commentService.findCommentsByKeyword(keyword);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Search comments by named entities
     */
    @GetMapping("/search-entities")
    public ResponseEntity<List<Comment>> searchByEntities(@RequestParam String entity) {
        logger.info("Searching comments by entity: {}", entity);
        List<Comment> comments = commentService.findCommentsByEntity(entity);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get comments by sentiment score range
     */
    @GetMapping("/sentiment-range")
    public ResponseEntity<List<Comment>> getCommentsBySentimentRange(
            @RequestParam double minScore, 
            @RequestParam double maxScore) {
        logger.info("Fetching comments with sentiment score between {} and {}", minScore, maxScore);
        List<Comment> comments = commentService.findCommentsBySentimentRange(minScore, maxScore);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get NLP statistics for a post
     */
    @GetMapping("/post/{postId}/nlp-stats")
    public ResponseEntity<Map<String, Object>> getNLPStatsForPost(@PathVariable String postId) {
        logger.info("Getting NLP statistics for post: {}", postId);
        Map<String, Object> stats = commentService.getNLPStatisticsForPost(postId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get a specific comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        logger.info("Fetching comment with ID: {}", id);
        Optional<Comment> comment = commentService.findCommentById(id);
        
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Update a comment (will re-analyze with Stanford CoreNLP)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment) {
        logger.info("Updating comment with ID: {}", id);
        
        Optional<Comment> existingComment = commentService.findCommentById(id);
        if (existingComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Comment commentToUpdate = existingComment.get();
        commentToUpdate.setContent(comment.getContent());
        commentToUpdate.setUpdatedAt(LocalDateTime.now());
        
        // Re-analyze with Stanford CoreNLP
        Comment updatedComment = commentService.analyzeAndSaveComment(commentToUpdate);
        return ResponseEntity.ok(updatedComment);
    }
    
    /**
     * Delete a comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        logger.info("Deleting comment with ID: {}", id);
        
        Optional<Comment> existingComment = commentService.findCommentById(id);
        if (existingComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Add a reply to a comment (with Stanford CoreNLP analysis)
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<Comment> addReply(@PathVariable String id, @RequestBody Comment reply) {
        logger.info("Adding reply to comment with ID: {}", id);
        
        Optional<Comment> parentComment = commentService.findCommentById(id);
        if (parentComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        reply.setParentCommentId(id);
        reply.setPostId(parentComment.get().getPostId());
        reply.setCreatedAt(LocalDateTime.now());
        
        // Analyze sentiment for the reply using Stanford CoreNLP
        Comment savedReply = commentService.analyzeAndSaveComment(reply);
        
        // Update parent comment with reply
        Comment updatedParent = commentService.addReplyToComment(id, savedReply);
        
        return new ResponseEntity<>(updatedParent, HttpStatus.CREATED);
    }
    
    /**
     * Like a comment
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable String id, @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        logger.info("User {} liking comment with ID: {}", userId, id);
        
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Comment> comment = commentService.findCommentById(id);
        if (comment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Comment updatedComment = commentService.likeComment(id, userId);
        return ResponseEntity.ok(updatedComment);
    }
    
    /**
     * Unlike a comment
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Comment> unlikeComment(@PathVariable String id, @RequestParam String userId) {
        logger.info("User {} unliking comment with ID: {}", userId, id);
        
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Comment> comment = commentService.findCommentById(id);
        if (comment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Comment updatedComment = commentService.unlikeComment(id, userId);
        return ResponseEntity.ok(updatedComment);
    }
    
    /**
     * Get replies for a comment
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Comment>> getRepliesByCommentId(@PathVariable String id) {
        logger.info("Fetching replies for comment: {}", id);
        List<Comment> replies = commentService.findRepliesByCommentId(id);
        return ResponseEntity.ok(replies);
    }
    
    /**
     * Search comments by content
     */
    @GetMapping("/search")
    public ResponseEntity<List<Comment>> searchComments(@RequestParam String query) {
        logger.info("Searching comments with query: {}", query);
        List<Comment> comments = commentService.searchCommentsByContent(query);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get most liked comments
     */
    @GetMapping("/most-liked")
    public ResponseEntity<List<Comment>> getMostLikedComments(
            @RequestParam(required = false) String postId,
            @RequestParam(defaultValue = "10") int limit) {
        
        if (postId != null && !postId.isEmpty()) {
            logger.info("Fetching {} most liked comments for post: {}", limit, postId);
            List<Comment> comments = commentService.findMostLikedCommentsByPostId(postId, limit);
            return ResponseEntity.ok(comments);
        } else {
            logger.info("Fetching {} most liked comments overall", limit);
            List<Comment> comments = commentService.findMostLikedComments(limit);
            return ResponseEntity.ok(comments);
        }
    }
    
    /**
     * Batch analyze unprocessed comments
     */
    @PostMapping("/batch-analyze")
    public ResponseEntity<Map<String, Object>> batchAnalyzeComments() {
        logger.info("Starting batch analysis of unprocessed comments");
        int processedCount = commentService.batchAnalyzeUnprocessedComments();
        
        Map<String, Object> response = Map.of(
            "message", "Batch analysis completed",
            "processedComments", processedCount,
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
}