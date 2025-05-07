package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder encoder;

    public boolean sendResetLink(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Use this link to reset your password:\n" +
                "http://localhost:4200/admin/authentication/reset-password?token=" + token);

        mailSender.send(message);

        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token).orElse(null);
        if (user == null) return false;

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
        return true;
    }
}
