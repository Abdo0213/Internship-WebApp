package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.model.Internship;
import com.example.internship_service.services.InternshipService;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    @JwtValidation(requiredRoles = {"student","admin"})
    public ResponseEntity<List<Internship>> getAllInternships() {
        return ResponseEntity.ok(internshipService.getAllInternships());
    }
    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr","student","admin"})
    public ResponseEntity<Internship> getOneInternship(@PathVariable Long id) {
        return ResponseEntity.ok(internshipService.getOneInternship(id));
    }
    @GetMapping("/active")
    @JwtValidation(requiredRoles = {"hr","student","admin"})
    public ResponseEntity<List<Internship>> getActiveInternships() {
        return ResponseEntity.ok(internshipService.getActiveInternships());
    }

    @GetMapping("/hr/{hrId}")
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<List<Internship>> getByHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(internshipService.getInternshipsByHr(hrId));
    }
    @PostMapping
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<?> createInternship(@RequestBody Internship internship, @RequestHeader("Authorization") String token) {
        try {
            Internship created = internshipService.createInternship(internship, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<String> updateInternship(@PathVariable Long id, @RequestBody Internship internship) {
        try {
            boolean isUpdated = internshipService.updateInternship(id, internship);
            if (isUpdated) {
                return ResponseEntity.ok("Internship updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<String> deleteInternship(@PathVariable Long id) {
        try {
            boolean isDeleted = internshipService.deleteInternship(id);

            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Internship deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Internship not found with ID: " + id);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

}
/*
Post create intern ----> create
get getAll --> get all (student)
get getwihthr --> get all intern with hr id == {id} (hr)
get oneIntern ---> get one intern (student)
Put Update --> update
Delete delete ---> delete
*/