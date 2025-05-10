package com.care4elders.paramedicalcare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "service_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequest {
    @Id
    private String id;
    private String userId; // From user service
    private String doctorId; // From user service
    private String serviceOfferingId;
    private String soignantId; // Will be populated when assigned
    private ServiceStatus status = ServiceStatus.PENDING;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private List<StatusUpdate> statusHistory;

    // Price at time of request (in case service price changes later)
    private BigDecimal agreedPrice;

    // Additional details
    private String specialInstructions;
    private Integer requiredDurationHours;

    @Data
    @AllArgsConstructor
    public static class StatusUpdate {
        private ServiceStatus status;
        private LocalDateTime updateTime;
        private String updatedBy; // Could be user/soignant ID
        private String serviceOfferingId;
        private LocalDateTime requestedAt;

        public StatusUpdate(ServiceStatus status, LocalDateTime now, String updatedBy) {
        }
    }
}