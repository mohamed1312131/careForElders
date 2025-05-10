package com.care4elders.paramedicalcare.service;

import com.care4elders.paramedicalcare.dto.ServiceAssignmentDTO;
import com.care4elders.paramedicalcare.dto.ServiceRequestDTO;
import com.care4elders.paramedicalcare.dto.ServiceStatusUpdateDTO;
import com.care4elders.paramedicalcare.dto.UserDTO;
import com.care4elders.paramedicalcare.entity.ServiceOffering;
import com.care4elders.paramedicalcare.entity.ServiceRequest;
import com.care4elders.paramedicalcare.entity.ServiceStatus;
import com.care4elders.paramedicalcare.repo.ServiceOfferingRepository;
import com.care4elders.paramedicalcare.repo.ServiceRequestRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ServiceRequestService.java
@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository repository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final RestTemplate restTemplate;

    public ServiceRequest createRequest(ServiceRequestDTO dto) {
        //validateUser(dto.getUserId());  // Uncomment after fixing user validation
        ServiceOffering service = serviceOfferingRepository.findById(dto.getServiceOfferingId())
                .orElseThrow(() -> new NotFoundException("Service not found"));

        // Add doctorId from the service offering
        ServiceRequest request = ServiceRequest.builder()
                .userId(dto.getUserId())
                .doctorId(service.getCreatedByDoctorId())  // Add this line
                .serviceOfferingId(dto.getServiceOfferingId())
                .status(ServiceStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .agreedPrice(service.getPricePerHour())
                .specialInstructions(dto.getSpecialInstructions())
                .requiredDurationHours(dto.getRequiredDurationHours())
                .statusHistory(new ArrayList<>())
                .build();

        return repository.save(request);
    }

    public ServiceRequest assignToSoignant(String requestId, ServiceAssignmentDTO dto) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        //validateSoignant(dto.getSoignantId());

        request.setSoignantId(dto.getSoignantId());
        request.setStatus(ServiceStatus.IN_PROGRESS);
        addStatusUpdate(request, ServiceStatus.IN_PROGRESS, dto.getSoignantId());

        return repository.save(request);
    }

    private void addStatusUpdate(ServiceRequest request, ServiceStatus status, String updatedBy) {
        request.getStatusHistory().add(new ServiceRequest.StatusUpdate(status, LocalDateTime.now(), updatedBy));
    }

    private void validateSoignant(String soignantId) {
        UserDTO user = restTemplate.getForObject(
                "http://user-service/users/{id}",
                UserDTO.class,
                soignantId
        );

        if (user == null || !"SOIGNANT".equals(user.getRole())) {
            throw new IllegalArgumentException("Invalid Soignant");
        }
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
    private void validateUser(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://user-service/users/{Id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"USER".equals(doctor.getRole())) {
                throw new NotFoundException("User not found or invalid role");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("USER not found with ID: " + doctorId);
        } catch (ResourceAccessException ex) {
            throw new NotFoundException("USER service unavailable");
        }
    }
    public List<ServiceOffering> getActiveServices() {
        return serviceOfferingRepository.findByActiveTrue()  // Corrected repository
                .stream()
                .filter(service -> true)  // Add any additional filtering logic here
                .collect(Collectors.toList());
    }
    public List<ServiceRequest> getRequestsByDoctor(String doctorId) {
        validateDoctor(doctorId); // Reuse your existing doctor validation
        return repository.findByDoctorId(doctorId);
    }
    public ServiceRequest updateStatus(String requestId, ServiceStatusUpdateDTO dto, String currentUserId) {
        ServiceRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Service request not found"));

        validateStatusTransition(request.getStatus(), dto.getNewStatus(), currentUserId);

        // Update status and history
        request.setStatus(dto.getNewStatus());
        addStatusUpdate(request, dto.getNewStatus(), currentUserId);

        // Handle completion timestamp
        if(dto.getNewStatus() == ServiceStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
        }

        return repository.save(request);
    }

    private void validateStatusTransition(ServiceStatus currentStatus, ServiceStatus newStatus, String userId) {
        UserDTO user = getUserFromUserService(userId);

        // Define allowed transitions based on role
        if(user.getRole().equals("SOIGNANT")) {
            validateSoignantTransition(currentStatus, newStatus);
        } else if(user.getRole().equals("DOCTOR")) {
            validateDoctorTransition(currentStatus, newStatus);
        } else {
            throw new IllegalStateException("Unauthorized role for status update");
        }
    }

    private void validateSoignantTransition(ServiceStatus current, ServiceStatus next) {
        if(current == ServiceStatus.IN_PROGRESS && next == ServiceStatus.COMPLETED) {
            return; // Valid transition
        }
        throw new IllegalStateException("Invalid status transition for Soignant: " + current + " -> " + next);
    }

    private void validateDoctorTransition(ServiceStatus current, ServiceStatus next) {
        if((current == ServiceStatus.PENDING && next == ServiceStatus.CANCELLED) ||
                (current == ServiceStatus.IN_PROGRESS && next == ServiceStatus.CANCELLED)) {
            return; // Valid doctor transitions
        }
        throw new IllegalStateException("Invalid status transition for Doctor: " + current + " -> " + next);
    }

    private UserDTO getUserFromUserService(String userId) {
        try {
            return restTemplate.getForObject(
                    "http://user-service/users/{id}",
                    UserDTO.class,
                    userId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }
    public List<ServiceRequest> getRequestsByUser(String userId) {

        return repository.findByUserId(userId);
    }
    public List<ServiceRequest> getRequestsByStatus(ServiceStatus status) {
        return repository.findByStatus(status);
    }

}
