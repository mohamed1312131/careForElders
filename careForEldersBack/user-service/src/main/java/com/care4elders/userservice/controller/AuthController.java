package com.care4elders.userservice.controller;

import com.care4elders.userservice.Config.JwtUtil;
import com.care4elders.userservice.dto.ApiResponse;
import com.care4elders.userservice.dto.AuthRequest;
import com.care4elders.userservice.dto.PasswordResetRequest;
import com.care4elders.userservice.dto.ResetPasswordRequest;
import com.care4elders.userservice.Service.EmailVerificationService;
import com.care4elders.userservice.Service.PasswordResetService;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository; // ✅ Add this line

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // If authentication is successful, generate JWT token
            String jwtToken = jwtUtil.generateToken(request.getEmail());

            // Retrieve user information from the repository
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            User user = userOptional.get();

            // Return JWT token along with user information
            return ResponseEntity.ok(Collections.singletonMap("data",
                    Map.of(
                            "token", jwtToken,
                            "user", Map.of(
                                    "id", user.getId(),
                                    "email", user.getEmail(),
                                    "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "profileImage", user.getProfileImage(), // Assuming this is a field in the User entity
                                    "roles", user.getRole()// Assuming the User entity has roles or similar
                            )
                    )
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid email or password"));
        }
    }

    // ✅ EMAIL VERIFICATION
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUserEmail(@RequestParam String token) {
        boolean success = emailVerificationService.verifyToken(token);
        if (success) {
            return ResponseEntity.ok("✅ Email verified! You can now login.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ Invalid or expired verification token.");
        }
    }

    @PostMapping("/request-reset")
    public ResponseEntity<ApiResponse> requestReset(@RequestBody PasswordResetRequest request) {
        boolean success = passwordResetService.sendResetLink(request.getEmail());
        if (success) {
            return ResponseEntity.ok(new ApiResponse("📧 Reset link sent to your email."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("❌ Email not found."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(new ApiResponse("✅ Password has been reset."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("❌ Invalid or expired reset token."));
        }
    }

}
