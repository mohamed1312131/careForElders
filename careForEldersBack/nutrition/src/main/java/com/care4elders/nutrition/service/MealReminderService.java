package com.care4elders.nutrition.service;

import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.entity.MealSchedule;
import com.care4elders.nutrition.repository.NutritionPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MealReminderService {

    private final EmailService emailService;
    private final NutritionPlanRepository nutritionPlanRepository;

    @Value("${app.reminders.enabled:true}")
    private boolean remindersEnabled;

    @Value("${app.reminders.advance-minutes:30}")
    private int advanceMinutes;

    // Constructor without circular dependency
    @Autowired
    public MealReminderService(
            EmailService emailService,
            NutritionPlanRepository nutritionPlanRepository) {
        this.emailService = emailService;
        this.nutritionPlanRepository = nutritionPlanRepository;
    }

    @Scheduled(fixedRate = 900000) // 15 minutes
    public void checkAndSendMealReminders() {
        if (!remindersEnabled) {
            log.debug("Meal reminders are disabled");
            return;
        }

        try {
            log.info("Checking for meal reminders to send...");

            List<NutritionPlan> activePlans = nutritionPlanRepository.findByActiveTrueAndEmailRemindersEnabledTrue();

            LocalTime currentTime = LocalTime.now();
            LocalTime reminderTime = currentTime.plusMinutes(advanceMinutes);

            log.info("Found {} active plans with email reminders enabled", activePlans.size());

            for (NutritionPlan plan : activePlans) {
                try {
                    checkAndSendRemindersForPlan(plan, currentTime, reminderTime);
                } catch (Exception e) {
                    log.error("Error processing reminders for plan: {}", plan.getId(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error in scheduled meal reminder check", e);
        }
    }

    private void checkAndSendRemindersForPlan(NutritionPlan plan, LocalTime currentTime, LocalTime reminderTime) {
        if (plan.getMealSchedule() == null) {
            log.debug("No meal schedule found for plan: {}", plan.getId());
            return;
        }

        MealSchedule schedule = plan.getMealSchedule();

        if (shouldSendReminder(schedule.getBreakfastTime(), currentTime, reminderTime)) {
            sendMealReminder(plan, "breakfast", schedule.getBreakfastTime());
        }

        if (shouldSendReminder(schedule.getLunchTime(), currentTime, reminderTime)) {
            sendMealReminder(plan, "lunch", schedule.getLunchTime());
        }

        if (shouldSendReminder(schedule.getDinnerTime(), currentTime, reminderTime)) {
            sendMealReminder(plan, "dinner", schedule.getDinnerTime());
        }

        if (schedule.getSnackTimes() != null) {
            for (String snackTime : schedule.getSnackTimes()) {
                if (shouldSendReminder(snackTime, currentTime, reminderTime)) {
                    sendMealReminder(plan, "snack", snackTime);
                }
            }
        }
    }

    private boolean shouldSendReminder(String mealTime, LocalTime currentTime, LocalTime reminderTime) {
        if (mealTime == null || mealTime.trim().isEmpty()) {
            return false;
        }

        try {
            LocalTime scheduledTime = LocalTime.parse(mealTime);
            return currentTime.isBefore(scheduledTime) &&
                    reminderTime.isAfter(scheduledTime.minusMinutes(5)) &&
                    reminderTime.isBefore(scheduledTime.plusMinutes(5));
        } catch (Exception e) {
            log.warn("Invalid meal time format: {}", mealTime);
            return false;
        }
    }

    @Async
    public CompletableFuture<Void> sendMealReminder(NutritionPlan plan, String mealType, String mealTime) {
        try {
            if (plan.getUserEmail() == null || plan.getUserEmail().trim().isEmpty()) {
                log.warn("No email address found for plan: {}", plan.getId());
                return CompletableFuture.completedFuture(null);
            }

            log.info("Sending {} reminder for plan: {} at time: {}", mealType, plan.getId(), mealTime);

            String subject = String.format("🍽️ %s Reminder - %s",
                    capitalize(mealType), mealTime);

            String message = createMealReminderMessage(plan, mealType, mealTime);

            emailService.sendHtmlEmail(plan.getUserEmail(), subject, message);

            plan.setLastReminderSent(LocalDateTime.now());
            nutritionPlanRepository.save(plan);

            log.info("Successfully sent {} reminder to: {}", mealType, plan.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send meal reminder for plan: {}", plan.getId(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    public void sendTestReminder(NutritionPlan plan, String mealType) {
        try {
            log.info("Sending test {} reminder for plan: {}", mealType, plan.getId());

            String mealTime = getCurrentMealTime(plan.getMealSchedule(), mealType);
            sendMealReminder(plan, mealType, mealTime);

        } catch (Exception e) {
            log.error("Failed to send test reminder", e);
            throw new RuntimeException("Failed to send test reminder", e);
        }
    }

    public void sendImmediateReminder(NutritionPlan plan, String mealType, String customMessage) {
        try {
            log.info("Sending immediate {} reminder for plan: {}", mealType, plan.getId());

            if (plan.getUserEmail() == null || plan.getUserEmail().trim().isEmpty()) {
                throw new RuntimeException("No email address found for plan");
            }

            String subject = String.format("🔔 Immediate %s Reminder", capitalize(mealType));
            String message = customMessage != null ? customMessage :
                    createMealReminderMessage(plan, mealType, "now");

            emailService.sendHtmlEmail(plan.getUserEmail(), subject, message);

            plan.setLastReminderSent(LocalDateTime.now());
            nutritionPlanRepository.save(plan);

            log.info("Successfully sent immediate {} reminder to: {}", mealType, plan.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send immediate reminder", e);
            throw new RuntimeException("Failed to send immediate reminder", e);
        }
    }

    private String getCurrentMealTime(MealSchedule schedule, String mealType) {
        if (schedule == null) {
            return LocalTime.now().toString();
        }

        return switch (mealType.toLowerCase()) {
            case "breakfast" -> schedule.getBreakfastTime() != null ? schedule.getBreakfastTime() : "08:00";
            case "lunch" -> schedule.getLunchTime() != null ? schedule.getLunchTime() : "13:00";
            case "dinner" -> schedule.getDinnerTime() != null ? schedule.getDinnerTime() : "19:00";
            case "snack" -> schedule.getSnackTimes() != null && !schedule.getSnackTimes().isEmpty() ?
                    schedule.getSnackTimes().get(0) : "15:00";
            default -> LocalTime.now().toString();
        };
    }

    private String createMealReminderMessage(NutritionPlan plan, String mealType, String mealTime) {
        String emoji = getMealEmoji(mealType);
        String greeting = getTimeBasedGreeting();

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }
                    .highlight { color: #4CAF50; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>%s %s Time Reminder!</h2>
                    </div>
                    <div class="content">
                        <p>%s!</p>
                        <p>It's time for your <span class="highlight">%s</span> at <span class="highlight">%s</span>.</p>
                        <p>Remember to follow your personalized nutrition plan for optimal health benefits.</p>
                        <div style="background: #e8f5e8; padding: 15px; border-radius: 5px; margin: 15px 0;">
                            <p><strong>💡 Nutrition Tip:</strong> Stay hydrated and eat mindfully to get the most from your meal!</p>
                        </div>
                        <p>Keep up the great work on your health journey!</p>
                    </div>
                    <div class="footer">
                        <p>Care4Elders Nutrition - Your Health, Our Priority</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                emoji, capitalize(mealType), greeting, mealType, mealTime);
    }

    private String getMealEmoji(String mealType) {
        return switch (mealType.toLowerCase()) {
            case "breakfast" -> "🌅";
            case "lunch" -> "🌞";
            case "dinner" -> "🌙";
            case "snack" -> "🍎";
            default -> "🍽️";
        };
    }

    private String getTimeBasedGreeting() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(12, 0))) {
            return "Good morning";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}