package com.care4elders.planandexercise.controller;


import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentRequestDTO;
import com.care4elders.planandexercise.DTO.addProgramToUser.ProgramAssignmentResponseDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.programDTO.ProgramResponseDTO;
import com.care4elders.planandexercise.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}