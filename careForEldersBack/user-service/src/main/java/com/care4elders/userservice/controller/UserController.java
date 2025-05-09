package com.care4elders.userservice.controller;

import com.care4elders.userservice.dto.UpdateUserRequest;
import com.care4elders.userservice.dto.UserRequest;
import com.care4elders.userservice.dto.UserResponse;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.care4elders.userservice.Service.EmailVerificationService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        User userEntity = userService.getUserEntityByEmail(createdUser.getEmail());
        emailVerificationService.sendVerificationEmail(userEntity); // Send email
        return ResponseEntity.ok(createdUser);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return Optional.ofNullable(userService.getUserById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id,
                                                   @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/doctors")
    public ResponseEntity<List<UserResponse>> getAllDoctors() {
        return ResponseEntity.ok(userService.getAllDoctors());
    }

}
