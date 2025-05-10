package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.dto.UserDTO;
import com.care4elders.appointmentavailability.entity.Reservation;
import com.care4elders.appointmentavailability.repository.IReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServiceImp implements IService {

    private final IReservationRepository reservationRepository;
    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_BASE_URL = "http://USER-SERVICE/users";

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> getReservationById(String id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation updateReservation(String id, Reservation updatedReservation) {
        return reservationRepository.findById(id)
                .map(existing -> {
                    existing.setDate(updatedReservation.getDate());
                    existing.setHeure(updatedReservation.getHeure());
                    existing.setStatut(updatedReservation.getStatut());
                    return reservationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));
    }

    @Override
    public void deleteReservation(String id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public Reservation AjouterReservation(Reservation reservation) {
        // Validate user existence via User Service
        UserDTO user = getUserById(reservation.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + reservation.getUserId());
        }

        return reservationRepository.save(reservation);
    }

    // === Communication with USER-SERVICE ===

    public List<UserDTO> getAllUsers() {
        try {
            UserDTO[] users = restTemplate.getForObject(USER_SERVICE_BASE_URL, UserDTO[].class);
            return users != null ? Arrays.asList(users) : List.of();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch users from User Service", e);
        }
    }

    public UserDTO getUserById(String userId) {
        try {
            return restTemplate.getForObject(USER_SERVICE_BASE_URL + "/" + userId, UserDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch user with ID: " + userId, e);
        }
    }

    public List<UserDTO> getAllDoctors() {
        try {
            UserDTO[] doctors = restTemplate.getForObject(
                    USER_SERVICE_BASE_URL + "/doctors",
                    UserDTO[].class
            );
            return doctors != null ? Arrays.asList(doctors) : List.of();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch doctors from User Service", e);
        }
    }

    public UserDTO getDoctorById(String doctorId) {
        UserDTO user = getUserById(doctorId);
        if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
            return user;
        } else {
            throw new RuntimeException("User with ID: " + doctorId + " is not a doctor.");
        }
    }
    @Override
    public List<Reservation> getReservationsByUserId(String userId) {
        // First verify the user exists
        UserDTO user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Then fetch their reservations
        return reservationRepository.findByUserId(userId);
    }
}
