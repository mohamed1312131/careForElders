package com.care4elders.blogforum;

import com.care4elders.blogforum.controller.CommentController;
import com.care4elders.blogforum.model.Comment;
import com.care4elders.blogforum.service.CommentService;
import com.care4elders.blogforum.service.StanfordNLPSentimentAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.time.LocalDateTime;
import java.util.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private StanfordNLPSentimentAnalysisService nlpService;

    // Helper to create a sample comment
    private Comment getSampleComment() {
        Comment comment = new Comment();
        comment.setId("1");
        comment.setPostId("post123");
        comment.setContent("Great post!");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setSentiment("POSITIVE");
        return comment;
    }

    // Test: POST /api/comments
    @Test
    public void testCreateComment() throws Exception {
        String requestJson = "{ \"postId\": \"post123\", \"content\": \"Great post!\" }";

        Comment savedComment = getSampleComment();

        when(commentService.analyzeAndSaveComment(any(Comment.class))).thenReturn(savedComment);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("Great post!"))
                .andDo(print());
    }

    // Test: GET /api/comments/{id}
    @Test
    public void testGetCommentById() throws Exception {
        String commentId = "1";
        Comment comment = getSampleComment();

        when(commentService.findCommentById(commentId)).thenReturn(Optional.of(comment));

        mockMvc.perform(get("/api/comments/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value("Great post!"))
                .andDo(print());
    }

    // Test: PUT /api/comments/{id}
    @Test
    public void testUpdateComment() throws Exception {
        String commentId = "1";
        String requestJson = "{ \"content\": \"Updated content\" }";

        Comment existingComment = getSampleComment();
        existingComment.setContent("Updated content");

        when(commentService.findCommentById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentService.analyzeAndSaveComment(any(Comment.class))).thenReturn(existingComment);

        mockMvc.perform(put("/api/comments/{id}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andDo(print());
    }

    // Test: DELETE /api/comments/{id}
    @Test
    public void testDeleteComment() throws Exception {
        String commentId = "1";

        Comment comment = getSampleComment();
        when(commentService.findCommentById(commentId)).thenReturn(Optional.of(comment));

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    // Test: POST /api/comments/{id}/analyze
    @Test
    public void testAnalyzeSentiment() throws Exception {
        String commentId = "1";

        Comment analyzedComment = getSampleComment();
        analyzedComment.setSentiment("STRONG_POSITIVE");

        when(commentService.findCommentById(commentId)).thenReturn(Optional.of(analyzedComment));
        when(commentService.analyzeAndSaveComment(any(Comment.class))).thenReturn(analyzedComment);

        mockMvc.perform(post("/api/comments/{id}/analyze", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sentiment").value("STRONG_POSITIVE"))
                .andDo(print());
    }

    // Test: POST /api/comments/{id}/reply
    @Test
    public void testAddReply() throws Exception {
        String parentId = "1";
        String requestJson = "{ \"content\": \"I agree with you.\" }";

        Comment parent = getSampleComment();
        Comment reply = new Comment();
        reply.setId("2");
        reply.setContent("I agree with you.");
        reply.setParentCommentId(parentId);
        reply.setCreatedAt(LocalDateTime.now());

        Comment updatedParent = getSampleComment();
        updatedParent.setReplies(Collections.singletonList(reply));

        when(commentService.findCommentById(parentId)).thenReturn(Optional.of(parent));
        when(commentService.analyzeAndSaveComment(any(Comment.class))).thenReturn(reply);
        when(commentService.addReplyToComment(eq(parentId), any(Comment.class))).thenReturn(updatedParent);

        mockMvc.perform(post("/api/comments/{id}/reply", parentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.replies[0].content").value("I agree with you."))
                .andDo(print());
    }

    // Test: GET /api/comments/post/{postId}
    @Test
    public void testGetCommentsByPostId() throws Exception {
        String postId = "post123";
        List<Comment> comments = Collections.singletonList(getSampleComment());

        when(commentService.findAllCommentsByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(postId))
                .andExpect(jsonPath("$[0].content").value("Great post!"))
                .andDo(print());
    }
}