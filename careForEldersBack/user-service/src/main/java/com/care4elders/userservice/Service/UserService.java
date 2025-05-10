package com.care4elders.userservice.Service;

import com.care4elders.userservice.dto.UpdateUserRequest;
import com.care4elders.userservice.dto.UserRequest;
import com.care4elders.userservice.dto.UserResponse;
import com.care4elders.userservice.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(String id, UpdateUserRequest request);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();
    void deleteUser(String id);
    UserResponse updateProfileImage(String userId, String imageUrl);
    User getUserEntityByEmail(String email);
    List<UserResponse> getAllDoctors();
    List<UserResponse> getUsersByRole(String role);
}
