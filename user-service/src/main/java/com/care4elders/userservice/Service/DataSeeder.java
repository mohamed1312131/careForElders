package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.Role;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User testUser = new User(
                    null,
                    "Alice",
                    "Smith",
                    "alice@example.com",
                    "secret123",
                    "555-1234",
                    Role.NORMAL_USER
            );
            userRepository.save(testUser);
            System.out.println("✅ Test user inserted!");
        }
    }
}
