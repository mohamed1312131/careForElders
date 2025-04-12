package com.care4elders.appointmentavailability.service;

import com.care4elders.appointmentavailability.entity.Disponibilite;

import com.care4elders.appointmentavailability.repository.IDisponibiliteRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class DispoService implements IDispoService {


    private IDisponibiliteRepository disponibiliteRepo;
    @Override
    public Disponibilite createDisponibilite(Disponibilite dispo) {
        return disponibiliteRepo.save(dispo);
    }

    @Override
    public List<Disponibilite> getAllDisponibilites() {
        return disponibiliteRepo.findAll();
    }
/*
    @Override
    public List<Disponibilite> getDisponibilitesByMedecin(Long medecinId) {
        return disponibiliteRepo.findByMedecinId(medecinId);
    }

    @Override
    public Disponibilite updateDisponibilite(Long id, Disponibilite newDispo) {
        return null;
    }

    @Override
    public void deleteDisponibilite(Long id) {
    }
*/
    //erride
 // public void deleteDisponibilite(Long id) {
    //  disponibiliteRepo.deleteById(id);
   //
}
