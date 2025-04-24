package com.care4elders.planandexercise.DTO.userDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorInfoDTO {
    private String doctorId;
    private String name;
    private String specialization;
}