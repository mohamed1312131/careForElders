package com.care4elders.appointmentavailability.Controller;

import com.care4elders.appointmentavailability.entity.Disponibilite;
import com.care4elders.appointmentavailability.service.DispoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/api/disponibilites")
@RestController
@AllArgsConstructor
public class DisponibiliteRestController {

    private DispoService dispoService;

    @PostMapping
    public ResponseEntity<Disponibilite> create(@RequestBody Disponibilite dispo) {
        Disponibilite saved = dispoService.createDisponibilite(dispo);
        return ResponseEntity.ok(saved);
    }
    @GetMapping
    public List<Disponibilite> getAllDisponibilites() {
        return dispoService.getAllDisponibilites();
    }
/*
    @GetMapping("/medecin/{id}")
    public ResponseEntity<List<Disponibilite>> getByMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(dispoService.getDisponibilitesByMedecin(id));
    }

  @PutMapping("/{id}")
    public ResponseEntity<Disponibilite> update(@PathVariable Long id, @RequestBody Disponibilite dispo) {
        return ResponseEntity.ok(dispoService.updateDisponibilite(id, dispo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dispoService.deleteDisponibilite(id);
        return ResponseEntity.noContent().build();
    }
*/

}

