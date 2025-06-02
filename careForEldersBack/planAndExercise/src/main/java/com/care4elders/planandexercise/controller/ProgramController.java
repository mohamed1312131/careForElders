package com.care4elders.planandexercise.controller;

import com.care4elders.planandexercise.DTO.*;
import com.care4elders.planandexercise.entity.Program;
import com.care4elders.planandexercise.entity.ProgramDay;
import com.care4elders.planandexercise.exception.*;
import com.care4elders.planandexercise.repository.ProgramDayRepository;
import com.care4elders.planandexercise.service.CloudinaryService;
import com.care4elders.planandexercise.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// ProgramController.java
@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProgramController {
    private final ProgramService programService;
    private final ProgramDayRepository programDayRepository;
    private final CloudinaryService cloudinaryService;
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProgram(
            @RequestPart("program") @Valid ProgramDTO programDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader("X-User-ID") String doctorId) {

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Upload to Cloudinary's "program_images" folder
                imageUrl = cloudinaryService.uploadImage(imageFile);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload image: " + e.getMessage());
            }
        }

        try {
            Program createdProgram = programService.createProgram(programDTO, doctorId, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProgram);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ServiceUnavailableException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }
    @DeleteMapping("/programs/{programId}/patients/{patientId}")
    public ResponseEntity<?> unassignPatient(
            @PathVariable String programId,
            @PathVariable String patientId,
            @RequestHeader("doctor-id") String doctorId // or however you're passing doctor identity
    ) {
        programService.unassignPatientFromProgram(programId, patientId, doctorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{programId}/days")
    public ResponseEntity<?> addDayToProgram(
            @PathVariable String programId,
            @Valid @RequestBody ProgramDayDTO dayDTO,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            Program updatedProgram = programService.addDayToProgram(programId, dayDTO, doctorId);
            return ResponseEntity.ok(updatedProgram);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (InvalidProgramStateException | InvalidDayConfigurationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    @GetMapping("/my-programs")
    public ResponseEntity<?> getPatientPrograms(
            @RequestHeader("X-User-ID") String patientId) {
        try {
            List<PatientProgramDTO> programs = programService.getProgramsByPatientId(patientId);
            return ResponseEntity.ok(programs);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @GetMapping("/{programId}/details")
    public ResponseEntity<?> getProgramDetailsWithProgress(
            @PathVariable String programId,
            @RequestHeader("X-User-ID") String patientId) {
        try {
            return ResponseEntity.ok(
                    programService.getProgramDetailsWithProgress(programId, patientId)
            );
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @GetMapping("/getAllPrograms")
    public ResponseEntity<List<ProgramListDTO>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }
    @GetMapping("/getProgramDetails/{programId}")
    public ResponseEntity<ProgramDetailsDTO> getProgramDetails(@PathVariable String programId) {
        return ResponseEntity.ok(programService.getProgramDetails(programId));
    }
    @GetMapping("/getPatients/{programId}/patients")
    public ResponseEntity<List<PatientAssignmentDTO>> getProgramPatients(@PathVariable String programId) {
        return ResponseEntity.ok(programService.getProgramPatients(programId));
    }
    @PutMapping("/{programId}/days/{dayId}")
    public ResponseEntity<?> updateProgramDay(
            @PathVariable String programId,
            @PathVariable String dayId,
            @Valid @RequestBody ProgramDayDTO dayDTO,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            ProgramDay updatedDay = programService.updateProgramDay(programId, dayId, dayDTO, doctorId);
            return ResponseEntity.ok(updatedDay);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteById/{programId}")
    public ResponseEntity<?> deleteProgram(@PathVariable String programId, @RequestHeader("X-User-ID") String doctorId) {
        try {
            programService.deleteProgram(programId, doctorId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{programId}/days/{dayId}")
    public ResponseEntity<?> deleteProgramDay(
            @PathVariable String programId,
            @PathVariable String dayId,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            programService.deleteProgramDay(programId, dayId, doctorId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @PutMapping("/update/{programId}")
    public ResponseEntity<?> updateProgram(
            @PathVariable String programId,
            @Valid @RequestBody ProgramDTO programDTO,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            Program updatedProgram = programService.updateProgram(programId, programDTO, doctorId);
            return ResponseEntity.ok(updatedProgram);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}