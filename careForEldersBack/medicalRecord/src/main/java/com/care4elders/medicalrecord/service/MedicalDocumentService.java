package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.entity.MedicalDocument;
import com.care4elders.medicalrecord.repository.MedicalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalDocumentService {

    @Autowired
    private MedicalDocumentRepository repository;

    public List<MedicalDocument> getDocumentsForUser(String userId) {
        return repository.findByUserId(userId);
    }

    public MedicalDocument saveDocument(MedicalDocument document) {
        return repository.save(document);
    }

    public void deleteDocument(String id) {
        repository.deleteById(id);
    }

    public MedicalDocument getDocumentById(String id) {
        return repository.findById(id).orElse(null);
    }

}
