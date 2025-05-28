package com.care4elders.appointmentavailability.Controller;


import com.care4elders.appointmentavailability.dto.UserDTO;
import com.care4elders.appointmentavailability.entity.Reservation;
import com.care4elders.appointmentavailability.service.IService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationRestController {

    private final IService reservationService;

    public ReservationRestController(IService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable String id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation created = reservationService.AjouterReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", e.getMessage()).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable String id, @RequestBody Reservation reservation) {
        try {
            Reservation updated = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Reservation>> getReservationsByDoctor(@PathVariable String doctorId) {
        try {
            return ResponseEntity.ok(reservationService.getReservationsByDoctorId(doctorId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }

    // User-related endpoints
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(reservationService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reservationService.getUserById(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<UserDTO>> getAllDoctors() {
        return ResponseEntity.ok(reservationService.getAllDoctors());
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<UserDTO> getDoctorById(@PathVariable String doctorId) {
        try {
            return ResponseEntity.ok(reservationService.getDoctorById(doctorId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("X-Error-Message", e.getMessage()).build();
        }
    }
}