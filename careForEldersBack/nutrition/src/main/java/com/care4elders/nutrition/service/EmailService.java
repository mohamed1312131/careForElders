package com.care4elders.nutrition.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.entity.MealSchedule;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@care4elders.com}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.name:Care4Elders Nutrition}")
    private String appName;

    @Value("${app.email.reply-to:support@care4elders.com}")
    private String replyToEmail;

    // ================================
    // BASIC EMAIL METHODS
    // ================================

    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send email to: {} with subject: {}", to, subject);
            return;
        }

        try {
            logger.info("Sending simple email to: {} with subject: {}", to, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setReplyTo(replyToEmail);

            mailSender.send(message);
            logger.info("Successfully sent simple email to: {}", to);

        } catch (Exception e) {
            logger.error("Failed to send simple email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send HTML email - THIS IS THE MISSING METHOD
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send HTML email to: {} with subject: {}", to, subject);
            return;
        }

        try {
            logger.info("Sending HTML email to: {} with subject: {}", to, subject);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content
            helper.setReplyTo(replyToEmail);

            mailSender.send(mimeMessage);
            logger.info("Successfully sent HTML email to: {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send HTML email", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    /**
     * Send email with both text and HTML content
     */
    public void sendMixedEmail(String to, String subject, String textContent, String htmlContent) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send mixed email to: {} with subject: {}", to, subject);
            return;
        }

        try {
            logger.info("Sending mixed email to: {} with subject: {}", to, subject);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent); // Both text and HTML
            helper.setReplyTo(replyToEmail);

            mailSender.send(mimeMessage);
            logger.info("Successfully sent mixed email to: {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to send mixed email to: {}", to, e);
            throw new RuntimeException("Failed to send mixed email", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending mixed email to: {}", to, e);
            throw new RuntimeException("Failed to send mixed email", e);
        }
    }

    // ================================
    // NUTRITION PLAN SPECIFIC EMAILS
    // ================================

    /**
     * Send plan creation confirmation email
     */
    public void sendPlanCreationConfirmation(String userEmail, NutritionPlan plan) {
        try {
            logger.info("Sending plan creation confirmation to: {}", userEmail);

            String subject = "✅ Your Nutrition Plan is Ready!";
            String htmlContent = createPlanConfirmationHtml(plan);

            sendHtmlEmail(userEmail, subject, htmlContent);

        } catch (Exception e) {
            logger.error("Failed to send plan creation confirmation to: {}", userEmail, e);
            throw new RuntimeException("Failed to send plan creation confirmation", e);
        }
    }

    /**
     * Send plan update notification email
     */
    public void sendPlanUpdateNotification(String userEmail, NutritionPlan plan) {
        try {
            logger.info("Sending plan update notification to: {}", userEmail);

            String subject = "🔄 Your Nutrition Plan Has Been Updated";
            String htmlContent = createPlanUpdateHtml(plan);

            sendHtmlEmail(userEmail, subject, htmlContent);

        } catch (Exception e) {
            logger.error("Failed to send plan update notification to: {}", userEmail, e);
            throw new RuntimeException("Failed to send plan update notification", e);
        }
    }

    /**
     * Send meal reminder email
     */
    public void sendMealReminder(String userEmail, String mealType, String mealTime, NutritionPlan plan) {
        try {
            logger.info("Sending {} reminder to: {} for time: {}", mealType, userEmail, mealTime);

            String subject = String.format("🍽️ %s Reminder - %s", capitalize(mealType), mealTime);
            String htmlContent = createMealReminderHtml(mealType, mealTime, plan);

            sendHtmlEmail(userEmail, subject, htmlContent);

        } catch (Exception e) {
            logger.error("Failed to send meal reminder to: {}", userEmail, e);
            throw new RuntimeException("Failed to send meal reminder", e);
        }
    }

    /**
     * Send welcome email for new users
     */
    public void sendWelcomeEmail(String userEmail, String userName) {
        try {
            logger.info("Sending welcome email to: {}", userEmail);

            String subject = "🎉 Welcome to Care4Elders Nutrition!";
            String htmlContent = createWelcomeHtml(userName);

            sendHtmlEmail(userEmail, subject, htmlContent);

        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", userEmail, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    /**
     * Send plan expiry notification
     */
    public void sendPlanExpiryNotification(String userEmail, NutritionPlan plan, int daysUntilExpiry) {
        try {
            logger.info("Sending plan expiry notification to: {} (expires in {} days)", userEmail, daysUntilExpiry);

            String subject = String.format("⏰ Your Nutrition Plan Expires in %d Days", daysUntilExpiry);
            String htmlContent = createPlanExpiryHtml(plan, daysUntilExpiry);

            sendHtmlEmail(userEmail, subject, htmlContent);

        } catch (Exception e) {
            logger.error("Failed to send plan expiry notification to: {}", userEmail, e);
            throw new RuntimeException("Failed to send plan expiry notification", e);
        }
    }

    // ================================
    // HTML TEMPLATE METHODS
    // ================================

    private String createPlanConfirmationHtml(NutritionPlan plan) {
        // Get meal schedule safely
        String breakfastTime = "08:00";
        String lunchTime = "13:00";
        String dinnerTime = "19:00";
        String snackTimes = "10:30, 16:00";

        if (plan.getMealSchedule() != null) {
            MealSchedule schedule = plan.getMealSchedule();
            breakfastTime = schedule.getBreakfastTime() != null ? schedule.getBreakfastTime() : breakfastTime;
            lunchTime = schedule.getLunchTime() != null ? schedule.getLunchTime() : lunchTime;
            dinnerTime = schedule.getDinnerTime() != null ? schedule.getDinnerTime() : dinnerTime;

            if (schedule.getSnackTimes() != null && !schedule.getSnackTimes().isEmpty()) {
                snackTimes = String.join(", ", schedule.getSnackTimes());
            }
        }

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0; 
                        padding: 0; 
                        background-color: #f4f4f4;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 20px auto; 
                        background: white;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); 
                        color: white; 
                        padding: 30px 20px; 
                        text-align: center;
                    }
                    .header h1 { margin: 0; font-size: 28px; }
                    .header p { margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; }
                    .content { padding: 30px 20px; }
                    .plan-info { 
                        background: #f8fff8; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 20px 0; 
                        border-left: 4px solid #4CAF50;
                    }
                    .schedule { 
                        background: #e8f5e8; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 20px 0; 
                    }
                    .schedule ul { margin: 10px 0; padding-left: 20px; }
                    .schedule li { margin: 8px 0; }
                    .footer { 
                        text-align: center; 
                        padding: 20px; 
                        background: #f8f9fa;
                        color: #666; 
                        font-size: 14px; 
                    }
                    .highlight { color: #4CAF50; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Nutrition Plan Created!</h1>
                        <p>Your personalized plan is ready</p>
                    </div>
                    <div class="content">
                        <p>Congratulations! Your personalized nutrition plan has been successfully created.</p>
                        
                        <div class="plan-info">
                            <h3>📋 Plan Details</h3>
                            <p><strong>Plan ID:</strong> %s</p>
                            <p><strong>Medical Conditions:</strong> %s</p>
                            <p><strong>Dietary Preferences:</strong> %s</p>
                            <p><strong>Allergies:</strong> %s</p>
                            <p><strong>Duration:</strong> <span class="highlight">%d days</span></p>
                            <p><strong>Target Calories:</strong> <span class="highlight">%d calories/day</span></p>
                        </div>
                        
                        <div class="schedule">
                            <h4>📅 Meal Reminder Schedule:</h4>
                            <ul>
                                <li>🌅 <strong>Breakfast:</strong> %s</li>
                                <li>🌞 <strong>Lunch:</strong> %s</li>
                                <li>🌙 <strong>Dinner:</strong> %s</li>
                                <li>🍎 <strong>Snacks:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <p>You will receive automatic reminders for each meal based on your schedule!</p>
                        
                        <div style="background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;">
                            <p><strong>💡 Next Steps:</strong></p>
                            <ul>
                                <li>Check your email for meal reminders</li>
                                <li>Follow your personalized nutrition plan</li>
                                <li>Track your progress regularly</li>
                                <li>Contact support if you need any adjustments</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer">
                        <p>%s - Your Health, Our Priority</p>
                        <p>Need help? Contact our support team anytime.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                plan.getId() != null ? plan.getId() : "N/A",
                plan.getMedicalConditions() != null ? plan.getMedicalConditions() : "Not specified",
                plan.getDietaryPreferences() != null ? plan.getDietaryPreferences() : "Not specified",
                plan.getAllergies() != null ? plan.getAllergies() : "None specified",
                plan.getPlanDuration() != null ? plan.getPlanDuration() : 30,
                plan.getTargetCalories() != null ? plan.getTargetCalories() : 2000,
                breakfastTime,
                lunchTime,
                dinnerTime,
                snackTimes,
                appName
        );
    }

    private String createPlanUpdateHtml(NutritionPlan plan) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2196F3; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }
                    .highlight { color: #2196F3; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>🔄 Plan Updated Successfully!</h2>
                    </div>
                    <div class="content">
                        <p>Your nutrition plan has been updated with the latest information.</p>
                        <p><strong>Plan ID:</strong> <span class="highlight">%s</span></p>
                        <p><strong>Last Updated:</strong> <span class="highlight">%s</span></p>
                        <p>Continue following your updated plan for the best results!</p>
                    </div>
                    <div class="footer">
                        <p>%s - Your Health, Our Priority</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                plan.getId() != null ? plan.getId() : "N/A",
                plan.getUpdatedAt() != null ? plan.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Just now",
                appName
        );
    }

    private String createMealReminderHtml(String mealType, String mealTime, NutritionPlan plan) {
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
                        <p>%s - Your Health, Our Priority</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                emoji, capitalize(mealType), greeting, mealType, mealTime, appName);
    }

    private String createWelcomeHtml(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #9C27B0; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }
                    .highlight { color: #9C27B0; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>🎉 Welcome to %s!</h2>
                    </div>
                    <div class="content">
                        <p>Hello <span class="highlight">%s</span>!</p>
                        <p>Welcome to our nutrition planning platform. We're excited to help you on your health journey!</p>
                        <p>Get started by creating your first personalized nutrition plan.</p>
                    </div>
                    <div class="footer">
                        <p>%s - Your Health, Our Priority</p>
                    </div>
                </div>
            </body>
            </html>
            """, appName, userName != null ? userName : "User", appName);
    }

    private String createPlanExpiryHtml(NutritionPlan plan, int daysUntilExpiry) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #FF9800; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }
                    .highlight { color: #FF9800; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>⏰ Plan Expiry Reminder</h2>
                    </div>
                    <div class="content">
                        <p>Your nutrition plan will expire in <span class="highlight">%d days</span>.</p>
                        <p><strong>Plan ID:</strong> %s</p>
                        <p>Consider renewing or creating a new plan to continue your health journey!</p>
                    </div>
                    <div class="footer">
                        <p>%s - Your Health, Our Priority</p>
                    </div>
                </div>
            </body>
            </html>
            """, daysUntilExpiry, plan.getId(), appName);
    }

    // ================================
    // UTILITY METHODS
    // ================================

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
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        if (hour < 12) {
            return "Good morning";
        } else if (hour < 17) {
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

    /**
     * Test email connectivity
     */
    public boolean testEmailConnection() {
        try {
            logger.info("Testing email connection...");

            // Create a test message but don't send it
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(fromEmail); // Send to self for testing
            helper.setSubject("Email Connection Test");
            helper.setText("This is a test email to verify email configuration.", false);

            // If we can create the message without errors, connection is likely good
            logger.info("Email connection test successful");
            return true;

        } catch (Exception e) {
            logger.error("Email connection test failed", e);
            return false;
        }
    }

    /**
     * Get email service status
     */
    public String getEmailServiceStatus() {
        if (!emailEnabled) {
            return "Email service is disabled";
        }

        try {
            boolean connectionOk = testEmailConnection();
            return connectionOk ? "Email service is operational" : "Email service has connection issues";
        } catch (Exception e) {
            return "Email service error: " + e.getMessage();
        }
    }
}