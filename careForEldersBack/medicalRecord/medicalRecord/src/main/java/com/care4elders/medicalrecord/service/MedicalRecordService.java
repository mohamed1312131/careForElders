package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.DTO.MedicalRecordDTO;
import com.care4elders.medicalrecord.entity.MedicalRecord;
import org.springframework.data.domain.Page;

public interface MedicalRecordService {
    MedicalRecord findByUserId(String userId);
    MedicalRecord save(MedicalRecord record);
    MedicalRecord update(String id, MedicalRecord record);

}
