package com.care4elders.appointmentavailability.Controller;

import com.care4elders.appointmentavailability.dto.DisponibiliteDTO;
import com.care4elders.appointmentavailability.dto.SlotDTO;
import com.care4elders.appointmentavailability.entity.Disponibilite;
import com.care4elders.appointmentavailability.service.DispoService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RequestMapping("/api/disponibilites")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DisponibiliteRestController {

    private DispoService disponibiliteService;

   // private final DisponibiliteService disponibiliteService;

    @PostMapping
    public ResponseEntity<Disponibilite> create(@RequestBody DisponibiliteDTO dto) {
        return new ResponseEntity<>(
                disponibiliteService.createDisponibilite(dto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Disponibilite> getByDoctor(@PathVariable String doctorId) {
        return disponibiliteService.getByDoctorId(doctorId);
    }

    @GetMapping("/{id}")
    public Disponibilite getById(@PathVariable String id) {
        return disponibiliteService.getById(id);
    }

    @PutMapping("/{id}")
    public Disponibilite update(
            @PathVariable String id,
            @RequestBody DisponibiliteDTO dto
    ) {
        return disponibiliteService.updateDisponibilite(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        disponibiliteService.deleteDisponibilite(id);
    }

    @GetMapping("/doctor/{doctorId}/range")
    public List<Disponibilite> getByDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return disponibiliteService.getByDoctorAndDateRange(doctorId, start, end);
    }
}