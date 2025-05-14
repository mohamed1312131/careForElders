package com.care4elders.paramedicalcare.repo;

import com.care4elders.paramedicalcare.entity.ServiceOffering;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceOfferingRepository extends MongoRepository<ServiceOffering, String> {
    List<ServiceOffering> findByCreatedByDoctorId(String doctorId);
    List<ServiceOffering> findByActiveTrue();

}