package com.care4elders.userservice.repository;

import com.care4elders.userservice.entity.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
    List<UserActivityLog> findByUserId(String userId);
}
