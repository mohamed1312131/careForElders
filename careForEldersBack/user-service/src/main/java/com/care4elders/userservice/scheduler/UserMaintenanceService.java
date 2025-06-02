package com.care4elders.userservice.scheduler;

import com.care4elders.userservice.Service.EmailService;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserMaintenanceService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserMaintenanceService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 * * * * ?") // Every day at 2 AM
    public void checkUserActivity() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getLastLogin() == null || !user.isEnabled()) continue;

            long monthsSinceLastLogin = ChronoUnit.MONTHS.between(user.getLastLogin(), now);

            if (monthsSinceLastLogin >= 12) {
                user.setEnabled(false);
                userRepository.save(user);
                System.out.println("❌ User " + user.getEmail() + " disabled due to inactivity.");
            } else if (monthsSinceLastLogin >= 6) {
                // Send warning
                emailService.sendEmail(
                        user.getEmail(),
                        "Inactive Account Warning",
                        "You haven’t logged in for 6 months. Please log in to avoid account deactivation."
                );
                System.out.println("⚠️ Reminder sent to " + user.getEmail());
            }
        }
    }
}
