package com.care4elders.planandexercise.controller;


import com.care4elders.planandexercise.DTO.DayCompletionDTO;
import com.care4elders.planandexercise.DTO.ProgramAssignmentDTO;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AssignmentController {
    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<?> assignProgram(
            @Valid @RequestBody ProgramAssignmentDTO assignmentDTO,
            @RequestHeader("X-User-ID") String doctorId) {
        try {
            return ResponseEntity.ok(
                    programService.assignProgramToPatient(assignmentDTO, doctorId)
            );
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }



    @PostMapping("/{assignmentId}/complete-day")
    public ResponseEntity<?> completeDay(
            @PathVariable String assignmentId,
            @RequestBody DayCompletionDTO completionDTO,
            @RequestHeader("X-User-ID") String patientId) {
        try {
            return ResponseEntity.ok(
                    programService.completeDay(assignmentId, completionDTO, patientId)
            );
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @GetMapping("/{assignmentId}/days/{dayNumber}")
    public ResponseEntity<?> getDayExercises(
            @PathVariable String assignmentId,
            @PathVariable int dayNumber,
            @RequestHeader("X-User-ID") String patientId) {
        try {
            return ResponseEntity.ok(
                    programService.getDayExercises(assignmentId, dayNumber, patientId)
            );
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}