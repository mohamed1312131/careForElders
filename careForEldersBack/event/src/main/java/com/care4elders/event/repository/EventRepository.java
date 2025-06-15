package com.care4elders.event.repository;

import com.care4elders.event.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByDateBetween(Date start, Date end);

}