package com.care4elders.planandexercise.repository;


import com.care4elders.planandexercise.entity.CareAssistant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareAssistantRepository extends MongoRepository<CareAssistant, String> {}
