package com.care4elders.paramedicalcare.repo;

import com.care4elders.paramedicalcare.entity.ParamedicalProfessional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParamedicalProfessionalRepository extends MongoRepository<ParamedicalProfessional, String> {
    List<ParamedicalProfessional> findBySpecialty(String specialty);
}
