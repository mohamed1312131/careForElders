package com.care4elders.paramedicalcare.controller;

import com.care4elders.paramedicalcare.dto.*;
import com.care4elders.paramedicalcare.entity.ServiceOffering;
import com.care4elders.paramedicalcare.entity.ServiceRequest;
import com.care4elders.paramedicalcare.entity.ServiceStatus;
import com.care4elders.paramedicalcare.service.ServiceOfferingService;
import com.care4elders.paramedicalcare.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ServiceController.java
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceOfferingService offeringService;
    private final ServiceRequestService requestService;

    // Service Offerings Endpoints
    @PostMapping("/offerings")
    public ResponseEntity<ServiceOffering> createServiceOffering(@RequestBody ServiceOfferingDTO dto) {
        return ResponseEntity.ok(offeringService.createService(dto));
    }

    @GetMapping("/offerings")
    public ResponseEntity<List<ServiceOffering>> getActiveServices() {
        return ResponseEntity.ok(offeringService.getActiveServices());
    }

    // Service Requests Endpoints
    @PostMapping("/requests")
    public ResponseEntity<ServiceRequest> createServiceRequest(@RequestBody ServiceRequestDTO dto) {
        return ResponseEntity.ok(requestService.createRequest(dto));
    }

    @PatchMapping("/requests/{id}/assign")
    public ResponseEntity<ServiceRequest> assignToSoignant(
            @PathVariable String id,
            @RequestBody ServiceAssignmentDTO dto) {
        return ResponseEntity.ok(requestService.assignToSoignant(id, dto));
    }

    @PatchMapping("/requests/{id}/status")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable String id,
            @RequestBody ServiceStatusUpdateDTO dto,
            @RequestHeader("X-Current-User") String currentUserId) {  // Add header parameter
        return ResponseEntity.ok(requestService.updateStatus(id, dto, currentUserId));
    }

    @GetMapping("/requests/doctor/{doctorId}")
    public ResponseEntity<List<ServiceRequest>> getDoctorRequests(@PathVariable String doctorId) {
        return ResponseEntity.ok(requestService.getRequestsByDoctor(doctorId));
    }
    @GetMapping("/offerings/{serviceId}/details")
    public ResponseEntity<ServiceOfferingDetails> getServiceDetails(
            @PathVariable String serviceId) {
        return ResponseEntity.ok(offeringService.getServiceDetails(serviceId));
    }
    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<List<ServiceRequest>> getUserRequests(
            @PathVariable String userId) {
        return ResponseEntity.ok(requestService.getRequestsByUser(userId));
    }
    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequest>> getRequestsByStatus(
            @RequestParam ServiceStatus status) {
        return ResponseEntity.ok(requestService.getRequestsByStatus(status));
    }
}