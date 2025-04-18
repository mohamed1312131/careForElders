package com.care4elders.appointmentavailability.repository;

import com.care4elders.appointmentavailability.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReservationRepository extends MongoRepository<Reservation, String> {
}
