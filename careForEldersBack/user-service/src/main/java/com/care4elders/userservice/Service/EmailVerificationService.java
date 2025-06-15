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
        user.setEnabled(false); // Disabled by default
        userRepository.save(user); // Save with token

        String subject = "Verify your email";
        String message = "Your verification token is: " + token; // 👈 OR include URL if needed

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject(subject);
        mail.setText(message);
        mailSender.send(mail);
    }

    public boolean verifyToken(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setVerificationToken(null); // clear token
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
