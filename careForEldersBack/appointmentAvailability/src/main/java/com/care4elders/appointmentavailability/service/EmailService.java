package com.care4elders.appointmentavailability.service;


<<<<<<< Updated upstream
=======
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
>>>>>>> Stashed changes
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service

public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendAppointmentConfirmation(
            String toEmail,
            String subject,
            String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ghassenbelkassem@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }



    public void sendAppointmentReminder(String toEmail, String patientName,
                                        String doctorName, String dateTime,
                                        String meetingLink, String location) {
        try {
            System.out.println("Scheduler is running at: " );
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ghassenbelkassem@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Reminder: Upcoming Appointment in 4 Hours");

            String htmlContent = buildReminderEmailContent(
                    patientName, doctorName, dateTime, meetingLink, location);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reminder email", e);
        }
    }

    private String buildReminderEmailContent(String patientName, String doctorName,
                                             String dateTime, String meetingLink,
                                             String location) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>")
                .append("<h2 style='color: #2c3e50;'>Appointment Reminder</h2>")
                .append("<p>Hello ").append(patientName).append(",</p>")
                .append("<p>This is a reminder that you have an appointment with Dr. ")
                .append(doctorName).append(" in 4 hours.</p>")
                .append("<p><strong>Time:</strong> ").append(dateTime).append("</p>");

        if (meetingLink != null && !meetingLink.isEmpty()) {
            html.append("<p><strong>Meeting Link:</strong> <a href='")
                    .append(meetingLink).append("'>Click to Join</a></p>");
        } else if (location != null && !location.isEmpty()) {
            html.append("<p><strong>Location:</strong> ").append(location).append("</p>");
        }

        html.append("<p style='margin-top: 20px;'>Thank you!</p>")
                .append("</body></html>");

        return html.toString();
    }
}