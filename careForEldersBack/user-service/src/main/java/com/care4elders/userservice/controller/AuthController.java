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
import com.care4elders.userservice.Service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private UserActivityLogService userActivityLogService;
    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Retrieve user
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            User user = userOptional.get();
            user.setLastLogin(LocalDateTime.now());

            userRepository.save(user);
            if (user.isTwoFactorEnabled()) {
                // Generate a temporary token for OTP validation (short expiry)
                String tempToken = jwtUtil.generateTempToken(user.getEmail()); // Implement this separately

                return ResponseEntity.ok(Map.of(
                        "requires2FA", true,
                        "tempToken", tempToken,
                        "userId", user.getId()
                ));
            } else {
                // 2FA not enabled, return normal JWT
                String jwtToken = jwtUtil.generateToken(request.getEmail());

                return ResponseEntity.ok(Map.of(
                        "requires2FA", false,
                        "token", jwtToken,
                        "user", Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "firstName", user.getFirstName(),
                                "lastName", user.getLastName(),
                                "phoneNumber", user.getPhoneNumber(),
                                "twoFactorEnabled", user.isTwoFactorEnabled(),
                                "roles", user.getRole()
                        )
                ));
            }
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
    @GetMapping("/auth/oauth-success")
    public ResponseEntity<?> oauth2Success(OAuth2AuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        // Create user if not exists, then generate JWT token
        String token = jwtUtil.generateToken(email);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "name", attributes.get("name"),
                "provider", authentication.getAuthorizedClientRegistrationId()
        ));
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean success = emailVerificationService.verifyToken(token);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Verified"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}
