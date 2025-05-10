package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.dto.UserDTO;
import com.care4elders.appointmentavailability.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface IService {
    public Reservation AjouterReservation(Reservation reservation);
    List<Reservation> getAllReservations();                       // Read All
    Optional<Reservation> getReservationById(String id);         // Read by ID
    Reservation updateReservation(String id, Reservation r);     // Update
    void deleteReservation(String id);
    List<Reservation> getReservationsByUserId(String userId);
  //  List<UserDTO> GetAllUsers();
    UserDTO getUserById(String userId);

    List<UserDTO> getAllUsers();
    List<UserDTO> getAllDoctors();
    UserDTO getDoctorById(String doctorId);
}
