package com.care4elders.appointmentavailability.service;


import com.care4elders.appointmentavailability.dto.UserDTO;

import com.care4elders.appointmentavailability.entity.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.care4elders.appointmentavailability.repository.IReservationRepository;

import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ServiceImp implements IService{
    IReservationRepository reservationRepository;
    private final RestTemplate restTemplate;

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
                    existing.setDate(r.getDate());
                    existing.setHeure(r.getHeure());
                    existing.setStatut(r.getStatut());
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
        }

        return reservationRepository.save(reservation);
    }
}
