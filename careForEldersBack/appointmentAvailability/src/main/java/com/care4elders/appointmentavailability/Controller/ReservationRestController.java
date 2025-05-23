package com.care4elders.appointmentavailability.Controller;

import com.care4elders.appointmentavailability.dto.UserDTO;
import com.care4elders.appointmentavailability.entity.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.care4elders.appointmentavailability.service.IService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")


public class ReservationRestController {
    IService Service;

    @PostMapping
    public Reservation AjouterReservation(@RequestBody Reservation reservation) {
        return Service.AjouterReservation(reservation);
    }

    @GetMapping
    public List<Reservation> getAll() {

        return Service.getAllReservations();
    }

    @GetMapping("/{id}")
    public Optional<Reservation> getById(@PathVariable String id) {

        return Service.getReservationById(id);
    }

    @PutMapping("/{id}")
    public Reservation update(@PathVariable String id, @RequestBody Reservation r) {
        return Service.updateReservation(id, r);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {

        Service.deleteReservation(id);
    }


    @GetMapping("/getAllUsers")
    public List<UserDTO> getAllUsers() {

        return Service.getAllUsers();
    }

    @GetMapping("/getUser/{userId}")
    public UserDTO getUserById(@PathVariable String userId) {

        return Service.getUserById(userId);
    }

    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<UserDTO>> getAllDoctors() {
        return ResponseEntity.ok(Service.getAllDoctors());
    }

    @GetMapping("/getDoctor/{doctorId}")
    public UserDTO getDoctorById(@PathVariable String doctorId) {
        return Service.getDoctorById(doctorId);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable String userId) {
        List<Reservation> reservations = Service.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }
}



