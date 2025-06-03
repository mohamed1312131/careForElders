package com.care4elders.medicalrecord.service;

import com.care4elders.medicalrecord.entity.MedicalNote;
import com.care4elders.medicalrecord.entity.MedicalRecord;
import com.care4elders.medicalrecord.repository.MedicalNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalNoteService {

    @Autowired
    private MedicalNoteRepository repository;

    public List<MedicalNote> getNotesForUser(String userId) {
        return repository.findByUserId(userId);
    }

    public MedicalNote addNote(MedicalNote note) {
        return repository.save(note);
    }

    public void deleteNote(String id) {
        repository.deleteById(id);
    }
    public Optional<MedicalNote> findById(String id) {
        return repository.findById(id);
    }
}
