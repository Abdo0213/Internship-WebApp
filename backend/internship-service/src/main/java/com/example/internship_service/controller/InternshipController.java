package com.example.internship_service.controller;

import com.example.internship_service.model.Internship;
import com.example.internship_service.services.InternshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @PostMapping
    public ResponseEntity<Internship> createInternship(@RequestBody Internship internship) {
        return ResponseEntity.ok(internshipService.createInternship(internship));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Internship>> getActiveInternships() {
        return ResponseEntity.ok(internshipService.getActiveInternships());
    }

    @GetMapping("/hr/{hrId}")
    public ResponseEntity<List<Internship>> getByHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(internshipService.getInternshipsByHr(hrId));
    }
}