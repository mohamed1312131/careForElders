package com.care4elders.planandexercise.controller;


import com.care4elders.planandexercise.DTO.ProgramRequestDTO;
import com.care4elders.planandexercise.DTO.ProgramResponseDTO;
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
}