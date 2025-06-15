package com.care4elders.userservice.controller;

import com.care4elders.userservice.Service.EmailService;
import com.care4elders.userservice.dto.EmailDTO;
import com.care4elders.userservice.dto.UpdateUserRequest;
import com.care4elders.userservice.dto.UserRequest;
import com.care4elders.userservice.dto.UserResponse;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.care4elders.userservice.Service.EmailVerificationService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @Autowired
    private EmailService emailService;
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
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    @GetMapping("/all-ids")
    public ResponseEntity<List<String>> getAllUserIds() {
        try {
            List<User> allUsers = userService.getAllUsersN();
            List<String> userIds = allUsers.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    @GetMapping("/{id}/emergency-info")
    public ResponseEntity<Map<String, String>> getUserEmergencyInfo(@PathVariable String id) {
        UserResponse  user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("emergencyContactName", user.getEmergencyContactName());
        response.put("emergencyContactPhone", user.getEmergencyContactPhone());
        response.put("emergencyContactEmail", user.getEmergencyContactEmail());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailDTO emailDTO) {
        try {
            emailService.sendEmail(
                    emailDTO.getTo(),
                    emailDTO.getSubject(),
                    emailDTO.getText()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/test-email")
    public ResponseEntity<Void> testEmail(@RequestParam String email) {
        try {
            emailService.sendEmail(
                    email,
                    "Test Email from Care4Elders",
                    "This is a test email to verify email functionality is working."
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
