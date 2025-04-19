package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.entity.Disponibilite;

import java.util.List;




public interface IDispoService {
    Disponibilite createDisponibilite(Disponibilite dispo);
    List<Disponibilite> getAllDisponibilites();
    //List<Disponibilite> getDisponibilitesByMedecin(Long medecinId);
   // Disponibilite updateDisponibilite(Long id, Disponibilite newDispo);
   // void deleteDisponibilite(Long id);

}
