package com.care4elders.medicalrecord.controller;

import com.care4elders.medicalrecord.entity.MedicalDocument;
import com.care4elders.medicalrecord.service.MedicalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/documents")
public class MedicalDocumentController {

    @Autowired
    private MedicalDocumentService service;

    @GetMapping("/user/{userId}")
    public List<MedicalDocument> getDocs(@PathVariable String userId) {
        return service.getDocumentsForUser(userId);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<ByteArrayResource> getDocumentContent(@PathVariable String id) {
        MedicalDocument document = service.getDocumentById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName() + "\"")
                .body(new ByteArrayResource(document.getData()));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            MedicalDocument document = new MedicalDocument();
            document.setUserId(userId);
            document.setFileName(file.getOriginalFilename());
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setData(file.getBytes());
            document.setUploadDate(LocalDate.now());

            service.saveDocument(document);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error uploading file");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
