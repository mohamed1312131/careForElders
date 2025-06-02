package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.entity.MedicalNote;
import com.care4elders.medicalrecord.service.MedicalNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/notes")
public class MedicalNoteController {

    @Autowired
    private MedicalNoteService service;

    @GetMapping("/user/{userId}")
    public List<MedicalNote> getNotes(@PathVariable String userId) {
        return service.getNotesForUser(userId);
    }

    @PostMapping
    public MedicalNote addNote(@RequestBody MedicalNote note) {
        return service.addNote(note);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteNote(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalNote> getMedicalNoteById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
