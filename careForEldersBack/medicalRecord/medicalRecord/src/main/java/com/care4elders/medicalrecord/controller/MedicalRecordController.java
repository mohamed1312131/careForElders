package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.entity.MedicalRecord;
import com.care4elders.medicalrecord.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/user/{userId}")
    public MedicalRecord getMedicalRecord(@PathVariable String userId) {
        return medicalRecordService.findByUserId(userId);
    }

    @PostMapping
    public MedicalRecord createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return medicalRecordService.save(medicalRecord);
    }

    @PutMapping("/{id}")
    public MedicalRecord updateMedicalRecord(@PathVariable String id, @RequestBody MedicalRecord medicalRecord) {
        return medicalRecordService.update(id, medicalRecord);
    }

}
