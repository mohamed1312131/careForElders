package com.care4elders.userservice.service;

import com.care4elders.userservice.dto.UserRequest;
import com.care4elders.userservice.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(String id, UserRequest request);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();
    void deleteUser(String id);
}
