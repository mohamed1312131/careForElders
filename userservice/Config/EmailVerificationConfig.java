package com.care4elders.userservice.Config;

import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public
class EmailVerificationConfig {

    @Autowired private JavaMailSender mailSender;
    @Autowired private UserRepository userRepository;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);  // You must add this field to User.java

        String verificationLink = "http://localhost:8081/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("Click this link to verify your email: " + verificationLink);
        mailSender.send(message);
    }

    public boolean verifyToken(String token) {
        User user = userRepository.findByVerificationToken(token).orElse(null);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
