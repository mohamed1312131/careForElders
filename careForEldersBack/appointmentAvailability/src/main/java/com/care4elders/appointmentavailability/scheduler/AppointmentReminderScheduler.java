package com.care4elders.appointmentavailability.scheduler;

import com.care4elders.appointmentavailability.entity.Reservation;
import com.care4elders.appointmentavailability.repository.IReservationRepository;
import com.care4elders.appointmentavailability.service.EmailService;
import com.care4elders.appointmentavailability.service.ServiceImp;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentReminderScheduler {
    private final IReservationRepository reservationRepository;
    private final EmailService emailService;
    private final ServiceImp userService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentReminderScheduler(IReservationRepository reservationRepository,
                                        EmailService emailService,
                                        ServiceImp userService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 */1 * * * *") // Runs every 15 minutes
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindowStart = now.plusHours(12);
        LocalDateTime reminderWindowEnd = reminderWindowStart.plusMinutes(15);

        List<Reservation> upcomingAppointments = reservationRepository
                .findUpcomingAppointmentsForReminder(reminderWindowStart, reminderWindowEnd);

        for (Reservation reservation : upcomingAppointments) {
            try {
                sendReminderForAppointment(reservation);
                reservation.setReminderSent(true);
                reservationRepository.save(reservation);
            } catch (Exception e) {
                // Log error but continue with other appointments
            }
        }
    }

    private void sendReminderForAppointment(Reservation reservation) {
        String patientEmail = userService.getUserById(reservation.getUserId()).getEmail();
        String doctorName = userService.getUserById(reservation.getDoctorId()).getFirstName();
        String patientName = userService.getUserById(reservation.getUserId()).getFirstName();

        String dateTime = reservation.getDate() + " " + reservation.getHeure();

        emailService.sendAppointmentReminder(
                patientEmail,
                patientName,
                doctorName,
                dateTime,
                reservation.getMeetingLink(),
                reservation.getLocation()
        );
    }
}

