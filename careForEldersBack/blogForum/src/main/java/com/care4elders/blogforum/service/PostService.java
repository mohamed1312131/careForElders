package com.care4elders.blogforum.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.care4elders.blogforum.dto.PostRequest;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    
    private final PostRepository postRepository;
    
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByPublishedTrue(pageable);
    }
    
    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }
    
    public List<Post> getPostsByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId);
    }
    
    public List<Post> searchPostsByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Post> searchPostsByContent(String content) {
        return postRepository.findByContentContainingIgnoreCase(content);
    }
    
    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTag(tag);
    }
    
    public Post createPost(PostRequest postRequest, String authorId, String authorName) {
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthorId(authorId);
        post.setAuthorName(authorName);
        post.setCreatedAt(new Date());
        post.setTags(postRequest.getTags());
        post.setPublished(postRequest.isPublished());
        
        log.info("Creating new post by author: {}", authorName);
        return postRepository.save(post);
    }
    
    public Post updatePost(String id, PostRequest postRequest) {
        Post post = getPostById(id);
        
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUpdatedAt(new Date());
        post.setTags(postRequest.getTags());
        post.setPublished(postRequest.isPublished());
        
        log.info("Updating post with id: {}", id);
        return postRepository.save(post);
    }
    
    public void deletePost(String id) {
        Post post = getPostById(id);
        log.info("Deleting post with id: {}", id);
        postRepository.delete(post);
    }
    
    public Post incrementViewCount(String id) {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }
}
