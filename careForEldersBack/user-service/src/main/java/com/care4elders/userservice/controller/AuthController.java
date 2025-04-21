package com.care4elders.userservice.controller;

import com.care4elders.userservice.Config.JwtUtil;
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
            System.out.println("🔐 Trying to authenticate " + request.getEmail());

            // 1. Load user manually before authentication
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            User user = optionalUser.get();

            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify your email before logging in.");
            }

            // 3. Proceed with authentication
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(request.getEmail());
            System.out.println("✅ Authentication successful for " + request.getEmail());

            return ResponseEntity.ok(Collections.singletonMap("token", token));

        } catch (AuthenticationException ex) {
            System.out.println("❌ Authentication failed for " + request.getEmail() + " - " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
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

    // 📩 REQUEST PASSWORD RESET
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest request) {
        boolean success = passwordResetService.sendResetLink(request.getEmail());
        if (success) {
            return ResponseEntity.ok("📧 Reset link sent to your email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Email not found.");
        }
    }

    // 🔁 PERFORM PASSWORD RESET
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("✅ Password has been reset.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid or expired reset token.");
        }
    }

}
