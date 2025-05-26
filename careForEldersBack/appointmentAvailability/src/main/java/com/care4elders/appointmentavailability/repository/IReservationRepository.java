package com.care4elders.appointmentavailability.repository;

import com.care4elders.appointmentavailability.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

<<<<<<< Updated upstream
@Repository
public interface IReservationRepository extends MongoRepository<Reservation, String> {
=======
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);

    List<Reservation> findByDoctorId(String doctorId);
    //List<Reservation> findBySoignantIdAndDate(String soignantId, LocalDate date);
>>>>>>> Stashed changes
}
