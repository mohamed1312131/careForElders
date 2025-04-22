package com.care4elders.planandexercise.repository;


import com.care4elders.planandexercise.entity.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {}
