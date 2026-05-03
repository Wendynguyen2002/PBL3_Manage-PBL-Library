package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import com.example.pblManagement.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/majors")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    // Admin: Create a major
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MajorSummaryDTO> createMajor(@Valid @RequestBody MajorRequestDTO dto) {
        MajorSummaryDTO createdMajor = majorService.createMajor(dto);
        return new ResponseEntity<>(createdMajor, HttpStatus.CREATED);
    }

    // Get details of a major
    @GetMapping("/{id}")
    public ResponseEntity<MajorSummaryDTO> getMajorById(@PathVariable String id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    // Get all majors with search and pagination
    @GetMapping
    public ResponseEntity<Page<MajorSummaryDTO>> getAllMajors(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        return ResponseEntity.ok(majorService.getAllMajors(search, pageable));
    }

    // Admin: Update a major
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MajorSummaryDTO> updateMajor(
            @PathVariable String id,
            @Valid @RequestBody MajorRequestDTO dto) {
        return ResponseEntity.ok(majorService.updateMajor(id, dto));
    }

    // Admin: Delete a major
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMajor(@PathVariable String id) {
        majorService.deleteMajor(id);
        return ResponseEntity.noContent().build();
    }
}
