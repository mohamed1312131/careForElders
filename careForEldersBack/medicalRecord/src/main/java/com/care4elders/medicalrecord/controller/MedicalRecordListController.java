package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.DTO.MedicalRecordDTO;
import com.care4elders.medicalrecord.entity.Appointment;
import com.care4elders.medicalrecord.entity.MedicalDocument;
import com.care4elders.medicalrecord.entity.MedicalNote;
import com.care4elders.medicalrecord.entity.MedicalRecord;
import com.care4elders.medicalrecord.repository.AppointmentRepository;
import com.care4elders.medicalrecord.repository.MedicalDocumentRepository;
import com.care4elders.medicalrecord.repository.MedicalNoteRepository;
import com.care4elders.medicalrecord.repository.MedicalRecordRepository;
import com.care4elders.medicalrecord.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordListController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepo;

    @GetMapping("/all")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable String id) {
        return medicalRecordRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


