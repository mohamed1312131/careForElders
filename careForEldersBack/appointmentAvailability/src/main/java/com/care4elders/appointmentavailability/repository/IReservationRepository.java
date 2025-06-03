package com.care4elders.appointmentavailability.repository;

import com.care4elders.appointmentavailability.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);

    List<Reservation> findByDoctorId(String doctorId);
    //List<Reservation> findBySoignantIdAndDate(String soignantId, LocalDate date);
    @Query("{ 'dateTime' : { $gte: ?0, $lte: ?1 }, 'reminderSent' : false, 'statut' : 'CONFIRME' }")
    List<Reservation> findUpcomingAppointmentsForReminder(LocalDateTime start, LocalDateTime end);
}
