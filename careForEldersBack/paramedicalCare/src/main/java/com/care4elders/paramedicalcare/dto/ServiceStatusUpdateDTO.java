package com.care4elders.paramedicalcare.dto;
import com.care4elders.paramedicalcare.entity.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStatusUpdateDTO {
    private ServiceStatus newStatus;
}