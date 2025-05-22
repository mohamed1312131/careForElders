package com.care4elders.blogforum.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.care4elders.blogforum.dto.PostRequest;
import com.care4elders.blogforum.exception.PostNotFoundException;
import com.care4elders.blogforum.model.LikePost;
import com.care4elders.blogforum.model.Post;
import com.care4elders.blogforum.repository.PostRepository;
import com.care4elders.blogforum.service.PostService;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
    }

    @Override
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByPublishedTrueAndIsDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Post getPostById(String id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    @Override
    public Post createPost(PostRequest postRequest, String authorId, String authorName) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .tags(postRequest.getTags())
                .build();
        
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(String id, PostRequest postRequest) throws PostNotFoundException {
        Post post = getPostById(id);
        
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setTags(postRequest.getTags());
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }

    @Override
    public void deletePost(String id) throws PostNotFoundException {
        Post post = getPostById(id);
        
        // Soft delete
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public List<Post> searchPostsByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(title);
    }

    @Override
    public List<Post> searchPostsByContent(String content) {
        return postRepository.findByContentContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(content);
    }

    @Override
    public List<Post> getPostsByTag(String tag) {
        return postRepository.findByTagsContainingAndPublishedTrueAndIsDeletedFalse(tag);
    }

    @Override
    public Post incrementViewCount(String id) throws PostNotFoundException {
        Post post = getPostById(id);
        
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    @Override
    public LikePost likePost(String id, String userId) throws PostNotFoundException {
        Post post = getPostById(id);
        
        // Check if user already liked the post
        Optional<LikePost> existingLike = post.getLikes().stream()
                .filter(like -> like.getUserId().equals(userId))
                .findFirst();
        
        if (existingLike.isPresent()) {
            return existingLike.get();
        }
        
        // Add new like
        LikePost like = LikePost.builder()
                .userId(userId)
                .build();
        
        post.getLikes().add(like);
        postRepository.save(post);
        
        return like;
    }

    @Override
    public void removeLike(String id, String userId) throws PostNotFoundException {
        Post post = getPostById(id);
        
        post.setLikes(
                post.getLikes().stream()
                        .filter(like -> !like.getUserId().equals(userId))
                        .toList()
        );
        
        postRepository.save(post);
    }

    @Override
    public int getLikesCount(Post post) {
        return post.getLikes().size();
    }

    @Override
    public Post getMostLikedPost() {
        List<Post> posts = postRepository.findByPublishedTrueAndIsDeletedFalse();
        
        return posts.stream()
                .max(Comparator.comparingInt(post -> post.getLikes().size()))
                .orElse(null);
    }

    @Override
    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    public void updateCommentsCount(String postId, long commentsCount) {
        try {
            Post post = getPostById(postId);
            post.setCommentsCount(commentsCount);
            postRepository.save(post);
        } catch (PostNotFoundException e) {
            // Log the error but don't throw it
            System.err.println("Could not update comments count for post: " + postId);
        }
    }
}