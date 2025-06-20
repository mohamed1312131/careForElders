package com.care4elders.blogforum;

import com.care4elders.blogforum.controller.PostController;

import com.care4elders.blogforum.dto.PostRequest;

import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.service.FileUploadService;
import com.care4elders.blogforum.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private FileUploadService fileUploadService;

    // Sample data
    private final String POST_ID = "1";
    private final String USER_ID = "user123";

    // Helper method to create a sample Post
    private Post getSamplePost() {
        Post post = new Post();
        post.setId(POST_ID);
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setAuthorId(USER_ID);
        post.setAuthorName("John Doe");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    private PostRequest getSamplePostRequest() {
        PostRequest request = new PostRequest();
        request.setTitle("New Post Title");
        request.setContent("Content of the new post.");
        request.setTags(Arrays.asList("health", "elderly"));
        return request;
    }

    // Test: GET /api/posts
    @Test
    public void testGetAllPosts() throws Exception {
        List<Post> posts = Arrays.asList(getSamplePost());
        Page<Post> page = new PageImpl<>(posts);

        when(postService.getPublishedPosts(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"))
                .andDo(print());
    }



    // Test: GET /api/posts/user/{userId}
    @Test
    public void testGetPostsByUser() throws Exception {
        List<Post> posts = Collections.singletonList(getSamplePost());

        when(postService.getPostsByUser(USER_ID)).thenReturn(posts);

        mockMvc.perform(get("/api/posts/user/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorId").value(USER_ID))
                .andDo(print());
    }

    // Test: GET /api/posts/search?title=...
    @Test
    public void testSearchPostsByTitle() throws Exception {
        List<Post> posts = Collections.singletonList(getSamplePost());

        when(postService.searchPostsByTitle("Test")).thenReturn(posts);

        mockMvc.perform(get("/api/posts/search")
                .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andDo(print());
    }

 @Autowired
private ObjectMapper objectMapper;
    // Test: POST /api/posts


   

    // Test: PUT /api/posts/{id}
    @Test
    public void testUpdatePost() throws Exception {
        PostRequest request = getSamplePostRequest();
        request.setTitle("Updated Title");

        Post updatedPost = getSamplePost();
        updatedPost.setTitle("Updated Title");

        when(postService.updatePost(eq(POST_ID), any(PostRequest.class))).thenReturn(updatedPost);

        mockMvc.perform(put("/api/posts/{id}", POST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Updated Title",
                      "content": "Updated content"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andDo(print());
    }

    // Test: DELETE /api/posts/{id}
    @Test
    public void testDeletePost() throws Exception {
        doNothing().when(postService).deletePost(POST_ID);

        mockMvc.perform(delete("/api/posts/{id}", POST_ID))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

 

    // Test: DELETE /api/posts/{id}/like
    @Test
    public void testRemoveLike() throws Exception {
        doNothing().when(postService).removeLike(POST_ID, USER_ID);

        mockMvc.perform(delete("/api/posts/{id}/like", POST_ID))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

 

    // Test: Validation errors in POST /api/posts
    @Test
    public void testCreatePost_withValidationErrors() throws Exception {
        PostRequest invalidRequest = new PostRequest(); // missing title and content

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[1].field").value("content"))
                .andDo(print());
    }

    // Test: GET /api/posts/most-liked
    @Test
    public void testGetMostLikedPost() throws Exception {
        Post post = getSamplePost();
        when(postService.getMostLikedPost()).thenReturn(post);

        mockMvc.perform(get("/api/posts/most-liked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andDo(print());
    }



    // Test: DELETE /api/posts/{postId}/images/{imageId}
    @Test
    public void testDeleteImageFromPost() throws Exception {
        doNothing().when(postService).removeImageFromPost(POST_ID, "image123");

        mockMvc.perform(delete("/api/posts/{postId}/images/{imageId}", POST_ID, "image123"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}