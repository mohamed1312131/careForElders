package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    private final Map<String, User> pendingUsers = new HashMap<>();

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        pendingUsers.put(token, user); // ⛔ Do not save to DB here!

        String subject = "Verify your email";
        String message = "Click to verify: http://localhost:8081/auth/verify?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }

    public boolean verifyToken(String token) {
        User user = pendingUsers.remove(token);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user); // ✅ Save only after verification
            return true;
        }
        return false;
    }
}
