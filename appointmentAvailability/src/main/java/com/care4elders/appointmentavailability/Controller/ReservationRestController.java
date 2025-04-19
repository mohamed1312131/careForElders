package com.care4elders.appointmentavailability.Controller;

import com.care4elders.appointmentavailability.entity.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.care4elders.appointmentavailability.service.IService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
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
}

