package com.care4elders.blogforum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.care4elders.blogforum.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    List<Post> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    Page<Post> findByPublishedTrueAndIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    List<Post> findByPublishedTrueAndIsDeletedFalse();
    
    List<Post> findByTitleContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(String title);
    
    List<Post> findByContentContainingIgnoreCaseAndPublishedTrueAndIsDeletedFalse(String content);
    
    List<Post> findByTagsContainingAndPublishedTrueAndIsDeletedFalse(String tag);
    
    List<Post> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(String authorId);
}