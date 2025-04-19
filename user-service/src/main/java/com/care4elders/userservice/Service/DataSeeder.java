package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.Role;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        long count = userRepository.count();
        System.out.println("Current user count: " + count);

        if (count == 0) {
            User adminUser = new User(
                    null,
                    "Admin",
                    "Smith",
                    "admin@example.com",
                    passwordEncoder.encode("admin"),
                    "555-1234",
                    Role.ADMINISTRATOR
            );
            userRepository.save(adminUser);
            System.out.println("✅ Admin user inserted with hashed password!");
        }
    }
}
