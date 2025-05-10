package com.care4elders.paramedicalcare.dto;

import com.care4elders.paramedicalcare.entity.ServiceOffering;
import com.care4elders.paramedicalcare.entity.ServiceRequest;

import java.util.List;


public record ServiceOfferingDetails(
        ServiceOffering service,
        List<ServiceRequest> recentRequests,
        long totalRequests,
        long completedRequests
) {}