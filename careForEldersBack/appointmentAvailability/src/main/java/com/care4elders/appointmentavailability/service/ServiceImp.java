package com.care4elders.appointmentavailability.service;


import com.care4elders.appointmentavailability.dto.UserDTO;

import com.care4elders.appointmentavailability.entity.Reservation;
<<<<<<< Updated upstream
=======
import com.care4elders.appointmentavailability.entity.StatutReservation;
import com.care4elders.appointmentavailability.entity.TypeReservation;
import com.care4elders.appointmentavailability.repository.IReservationRepository;
>>>>>>> Stashed changes
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.care4elders.appointmentavailability.repository.IReservationRepository;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ServiceImp implements IService{
    IReservationRepository reservationRepository;
    private final RestTemplate restTemplate;
    private final EmailService emailService;

    /*@Override
    public Reservation AjouterReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }*/

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> getReservationById(String id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation updateReservation(String id, Reservation r) {
        return reservationRepository.findById(id)
                .map(existing -> {
<<<<<<< Updated upstream
                    existing.setDate(r.getDate());
                    existing.setHeure(r.getHeure());
                    existing.setStatut(r.getStatut());
=======
                    existing.setDate(updatedReservation.getDate());
                    existing.setHeure(updatedReservation.getHeure());
                    existing.setEndTime(updatedReservation.getEndTime());
                    existing.setStatut(updatedReservation.getStatut());
                    existing.setUserId(updatedReservation.getUserId());
                    existing.setDoctorId(updatedReservation.getDoctorId());
                    existing.setReservationType(updatedReservation.getReservationType());
                    existing.setMeetingLink(updatedReservation.getMeetingLink());
                    existing.setLocation(updatedReservation.getLocation());
>>>>>>> Stashed changes
                    return reservationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    @Override
    public void deleteReservation(String id) {
        reservationRepository.deleteById(id);
    }



    @Override
    public Reservation AjouterReservation(Reservation reservation) {
<<<<<<< Updated upstream
        // Assume reservation contains a userId field (you can add it if it doesn't exist)
        String userId = reservation.getUserId();

        // Fetch user from user-service
        String userServiceUrl = "http://USER-SERVICE/users/" + userId;

        try {
            UserDTO user = restTemplate.getForObject(userServiceUrl, UserDTO.class);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("User not found in user-service", e);
=======
        // 1. Validate user & doctor exist
        UserDTO user = getUserById(reservation.getUserId());
        UserDTO doctor = getUserById(reservation.getDoctorId());

        if (user == null || doctor == null) {
            throw new RuntimeException("User or doctor not found");
>>>>>>> Stashed changes
        }

        // 2. Validate appointment type rules
        if (reservation.getReservationType() == null) {
            throw new IllegalArgumentException("Appointment type is required");
        }

        if (reservation.getReservationType() == TypeReservation.EN_LIGNE) {
            if (reservation.getMeetingLink() == null || reservation.getMeetingLink().isBlank()) {
                throw new IllegalArgumentException("Meeting link is required for online appointments");
            }
        } else if (reservation.getReservationType() == TypeReservation.PRESENTIEL) {
            if (reservation.getLocation() == null || reservation.getLocation().isBlank()) {
                throw new IllegalArgumentException("Location is required for in-person appointments");
            }
        }

        // Set default status if not provided
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.CONFIRME);
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        // Send confirmation emails (now includes meetingLink/location as appropriate)
        sendConfirmationEmails(savedReservation, user, doctor);

        return savedReservation;

    }

    private void sendConfirmationEmails(Reservation reservation, UserDTO user, UserDTO doctor) {
        // Email to Patient
        String patientEmail = user.getEmail();
        String patientSubject = "Your Appointment Confirmation";
        StringBuilder patientBody = new StringBuilder();
        patientBody.append(String.format(
                "Hello %s,\n\nYour appointment with Dr. %s has been confirmed.\n\n" +
                        "Date: %s\nTime: %s\nType: %s\n",
                user.getFirstName(),
                doctor.getFirstName(),
                reservation.getDate(),
                reservation.getHeure(),
                reservation.getReservationType()
        ));
        // Add meeting link or location based on type
        if (reservation.getReservationType() == TypeReservation.EN_LIGNE && reservation.getMeetingLink() != null) {
            patientBody.append("Meeting Link: ").append(reservation.getMeetingLink()).append("\n");
        } else if (reservation.getReservationType() == TypeReservation.PRESENTIEL && reservation.getLocation() != null) {
            patientBody.append("Location: ").append(reservation.getLocation()).append("\n");
        }
        patientBody.append("\nThank you!");

        emailService.sendAppointmentConfirmation(patientEmail, patientSubject, patientBody.toString());

        // Email to Doctor
        String doctorEmail = doctor.getEmail();
        String doctorSubject = "New Appointment Booking";
        StringBuilder doctorBody = new StringBuilder();
        doctorBody.append(String.format(
                "Dr. %s,\n\nA new appointment has been booked:\n\n" +
                        "Patient: %s %s\nDate: %s\nTime: %s\nType: %s\n",
                doctor.getFirstName(),
                user.getFirstName(),
                user.getLastName(),
                reservation.getDate(),
                reservation.getHeure(),
                reservation.getReservationType()
        ));
        // Add meeting link or location based on type
        if (reservation.getReservationType() == TypeReservation.EN_LIGNE && reservation.getMeetingLink() != null) {
            doctorBody.append("Meeting Link: ").append(reservation.getMeetingLink()).append("\n");
        } else if (reservation.getReservationType() == TypeReservation.PRESENTIEL && reservation.getLocation() != null) {
            doctorBody.append("Location: ").append(reservation.getLocation()).append("\n");
        }

        emailService.sendAppointmentConfirmation(doctorEmail, doctorSubject, doctorBody.toString());
    }

<<<<<<< Updated upstream
    


=======
    // === Communication with USER-SERVICE ===
    @Override
>>>>>>> Stashed changes
    public List<UserDTO> getAllUsers() {
        String userServiceUrl = "http://USER-SERVICE/users"; // Remove trailing slash
          try {        UserDTO[] users = restTemplate.getForObject(userServiceUrl, UserDTO[].class);
              assert users != null;
              return Arrays.asList(users);    }
          catch (RestClientException e) {
              throw new RuntimeException("Failed to fetch users from User Service", e);
          }
    }

    @Override
    public UserDTO getUserById(String userId) {
        String url = "http://USER-SERVICE/users/" + userId;
        try {
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch user with ID: " + userId, e);
        }
    }

    @Override
    public List<UserDTO> getAllDoctors() {
        List<UserDTO> allUsers = getAllUsers(); // uses restTemplate
        return allUsers.stream()
                .filter(user -> "DOCTOR".equalsIgnoreCase(user.getRole()))
                .toList(); // Or .collect(Collectors.toList()) in Java 8
    }

    @Override
    public UserDTO getDoctorById(String doctorId) {
        UserDTO user = getUserById(doctorId);
        if (user != null && "DOCTOR".equalsIgnoreCase(user.getRole())) {
            return user;
<<<<<<< Updated upstream
        } else {
            throw new RuntimeException("Doctor not found with ID: " + doctorId);
=======
>>>>>>> Stashed changes
        }
        throw new RuntimeException("User with ID: " + doctorId + " is not a doctor or doesn't exist.");
    }

<<<<<<< Updated upstream
    }
=======
    @Override
    public List<Reservation> getReservationsByUserId(String userId) {
        UserDTO user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> getReservationsByDoctorId(String doctorId) {
        UserDTO doctor = getDoctorById(doctorId);
        if (doctor == null) {
            throw new RuntimeException("Doctor not found with ID: " + doctorId);
        }
        return reservationRepository.findByDoctorId(doctorId);
    }
}
>>>>>>> Stashed changes
