package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.DTO.MedicalRecordDTO;
import com.care4elders.medicalrecord.entity.MedicalRecord;
import com.care4elders.medicalrecord.repository.AppointmentRepository;
import com.care4elders.medicalrecord.repository.MedicalDocumentRepository;
import com.care4elders.medicalrecord.repository.MedicalNoteRepository;
import com.care4elders.medicalrecord.repository.MedicalRecordRepository;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {
  // Add this

    @Autowired
    private MedicalRecordRepository repository;

    @Override
    public MedicalRecord findByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElse(null); // or throw an exception if preferred
    }

    @Override
    public MedicalRecord save(MedicalRecord record) {
        return repository.save(record);
    }

    @Override
    public MedicalRecord update(String id, MedicalRecord updatedRecord) {
        Optional<MedicalRecord> existing = repository.findById(id);
        if (existing.isPresent()) {
            MedicalRecord record = existing.get();

            record.setBloodType(updatedRecord.getBloodType());
            record.setAllergies(updatedRecord.getAllergies());
            record.setCurrentMedications(updatedRecord.getCurrentMedications());
            record.setChronicConditions(updatedRecord.getChronicConditions());
            record.setFamilyMedicalHistory(updatedRecord.getFamilyMedicalHistory());
            record.setPrimaryCarePhysician(updatedRecord.getPrimaryCarePhysician());
            record.setInsuranceInformation(updatedRecord.getInsuranceInformation());
            record.setLastPhysicalExam(updatedRecord.getLastPhysicalExam());
            record.setVaccinations(updatedRecord.getVaccinations());
            record.setNotes(updatedRecord.getNotes());
            record.setDocuments(updatedRecord.getDocuments());

            return repository.save(record);
        } else {
            return null; // or throw an exception
        }
    }


}
