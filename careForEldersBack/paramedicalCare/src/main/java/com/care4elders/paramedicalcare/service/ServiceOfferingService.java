package com.care4elders.paramedicalcare.service;

import com.care4elders.paramedicalcare.dto.ServiceOfferingDTO;
import com.care4elders.paramedicalcare.dto.ServiceOfferingDetails;
import com.care4elders.paramedicalcare.dto.UserDTO;
import com.care4elders.paramedicalcare.entity.ServiceOffering;
import com.care4elders.paramedicalcare.entity.ServiceRequest;
import com.care4elders.paramedicalcare.entity.ServiceStatus;
import com.care4elders.paramedicalcare.repo.ServiceOfferingRepository;
import com.care4elders.paramedicalcare.repo.ServiceRequestRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

// ServiceOfferingService.java
@Service
@RequiredArgsConstructor
public class ServiceOfferingService {
    private final ServiceOfferingRepository repository;
    private final ServiceRequestRepository requestRepository;
    private final RestTemplate restTemplate;

    public ServiceOffering createService(ServiceOfferingDTO dto) {
        //validateDoctor(dto.getCreatedByDoctorId());

        ServiceOffering service = ServiceOffering.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .pricePerHour(dto.getPricePerHour())
                .category(dto.getCategory())
                .createdByDoctorId(dto.getCreatedByDoctorId())
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(service);
    }

    private void validateDoctor(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://user-service/users/{Id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
                throw new NotFoundException("Doctor not found or invalid role");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Doctor not found with ID: " + doctorId);
        } catch (ResourceAccessException ex) {
            throw new NotFoundException("User service unavailable");
        }
    }
    public List<ServiceOffering> getActiveServices() {
        return repository.findAll();
    }
    public ServiceOfferingDetails getServiceDetails(String serviceId) {
        ServiceOffering service = repository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        // Get recent requests with pagination
        List<ServiceRequest> recentRequests = requestRepository.findTop5ByServiceOfferingIdOrderByRequestedAtDesc(
                serviceId,
                (Pageable) PageRequest.of(0, 5)
        );

        long totalRequests = requestRepository.countByServiceOfferingId(serviceId);
        long completedRequests = requestRepository.countByServiceOfferingIdAndStatus(
                serviceId,
                ServiceStatus.COMPLETED
        );

        return new ServiceOfferingDetails(service, recentRequests, totalRequests, completedRequests);
    }
}

