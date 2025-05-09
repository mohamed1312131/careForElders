package com.care4elders.userservice.repository;

import com.care4elders.userservice.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken); // ✅ Add this line
    boolean existsByEmail(String email);
    List<User> getUsersByRole(String role);
}
