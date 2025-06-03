package com.care4elders.blogforum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.care4elders.blogforum.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    List<Post> findByAuthorId(String authorId);
    
    Page<Post> findByPublishedTrue(Pageable pageable);
    
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Post> findByTitleContainingIgnoreCase(String title);
    
    @Query("{'content': {$regex: ?0, $options: 'i'}}")
    List<Post> findByContentContainingIgnoreCase(String content);
    
    @Query("{'tags': {$in: [?0]}}")
    List<Post> findByTag(String tag);
}
