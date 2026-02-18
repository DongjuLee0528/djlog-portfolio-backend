package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.EducationResponse;
import com.example.djlogportfoliobackend.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducations() {
        return ResponseEntity.ok(educationService.getAllEducations());
    }

    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable UUID id, @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(educationService.updateEducation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable UUID id) {
        educationService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }
}