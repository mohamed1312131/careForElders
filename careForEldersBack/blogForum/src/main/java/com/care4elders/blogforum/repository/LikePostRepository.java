package com.care4elders.blogforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.care4elders.blogforum.model.LikePost;

@Repository
public interface LikePostRepository extends MongoRepository<LikePost, String> {
    List<LikePost> findByPostId(String postId);
    Optional<LikePost> findByPostIdAndUserId(String postId, String userId);
    long countByPostIdAndIsLikeTrue(String postId);
    long countByPostIdAndIsLikeFalse(String postId);
    void deleteByPostIdAndUserId(String postId, String userId);
}