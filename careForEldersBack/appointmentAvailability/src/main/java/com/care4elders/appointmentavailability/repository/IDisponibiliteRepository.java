package com.care4elders.appointmentavailability.repository;

import com.care4elders.appointmentavailability.entity.Disponibilite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
@Repository

public interface IDisponibiliteRepository extends MongoRepository<Disponibilite, String> {
    List<Disponibilite> findByDoctorId(String doctorId);
<<<<<<< Updated upstream

    // List<Disponibilite> findByMedecinId(Long medecinId);
     //   List<Disponibilite> findByMedecinIdAndJour(Long medecinId, DayOfWeek jour);

=======
    List<Disponibilite> findByDoctorIdAndDate(String doctorId, LocalDate date);
    List<Disponibilite> findByDoctorIdAndDateBetween(String doctorId, LocalDate start, LocalDate end);
>>>>>>> Stashed changes
}
