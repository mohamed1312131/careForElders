package com.care4elders.paramedicalcare.repo;

import com.care4elders.paramedicalcare.entity.ServiceRequest;
import com.care4elders.paramedicalcare.entity.ServiceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {
    List<ServiceRequest> findByStatus(ServiceStatus status);
    List<ServiceRequest> findBySoignantId(String soignantId);
    List<ServiceRequest> findByDoctorId(String doctorId);
    Optional<ServiceRequest> findByIdAndDoctorId(String id, String doctorId);
    @Query(value = "{ 'serviceOfferingId': ?0 }", sort = "{ 'requestedAt': -1 }")
    List<ServiceRequest> findTop5ByServiceOfferingIdOrderByRequestedAtDesc(String serviceOfferingId, Pageable pageable);

    @Query(value = "{ 'serviceOfferingId': ?0 }", count = true)
    long countByServiceOfferingId(String serviceOfferingId);

    @Query(value = "{ 'serviceOfferingId': ?0, 'status': ?1 }", count = true)
    long countByServiceOfferingIdAndStatus(String serviceOfferingId, ServiceStatus status);

    List<ServiceRequest> findByUserId(String userId);

}
