package com.care4elders.planandexercise.controller;


import com.care4elders.planandexercise.DTO.ExerciseCompletion.ExerciseCompletionDTO;
import com.care4elders.planandexercise.DTO.ExerciseCompletion.ExerciseCompletionRequestDTO;
import com.care4elders.planandexercise.DTO.PatientProgramHistoryDTO.PatientProgramHistoryDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentRequestDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentResponseDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramResponseDTO;
import com.care4elders.planandexercise.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramResponseDTO> createProgram(
            @Valid @RequestBody ProgramRequestDTO programRequest
    ) {
        ProgramResponseDTO response = programService.createProgram(programRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/assign")
    public ResponseEntity<ProgramAssignmentResponseDTO> assignProgram(
            @Valid @RequestBody ProgramAssignmentRequestDTO request
    ) {
        ProgramAssignmentResponseDTO response = programService.assignProgramToPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/patient/{patientId}/current")
    public List<ProgramAssignmentResponseDTO> getCurrentPrograms(
            @PathVariable String patientId
    ) {
        return programService.getCurrentPatientPrograms(patientId);
    }


    @PostMapping("/complete")
    public ResponseEntity<ProgramAssignmentResponseDTO> completeProgram(
            @RequestParam String patientId,
            @RequestParam String programId
    ) {
        ProgramAssignmentResponseDTO response = programService.completeProgram(patientId, programId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<PatientProgramHistoryDTO>> getPatientProgramHistory(
            @PathVariable String patientId
    ) {
        List<PatientProgramHistoryDTO> history = programService.getPatientProgramHistory(patientId);
        return ResponseEntity.ok(history);
    }
}