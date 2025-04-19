package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.User;

public interface IEmailService {
    void sendVerificationEmail(User user);
    void sendPasswordResetEmail(User user);
}
