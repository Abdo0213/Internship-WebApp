package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.dto.ApplicationRequest;
import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<?> createApplication(@RequestBody ApplicationRequest request, @RequestHeader("Authorization") String token) {
        try {
            Application application = applicationService.createApplication(
                    request.getStudentId(),
                    request.getInternshipId(),
                    token
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // for student
    @GetMapping("/student/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<List<Application>> getStudentApplications(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getStudentApplications(id));
    }

    // get one application student or hr
    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr","student"})
    public ResponseEntity<Application> getOneApplication(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getOneApplication(id));
    }

    @GetMapping("/hr/{hrId}")
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<List<Application>> getApplicationsByHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(applicationService.getApplicationsByHr(hrId));
    }

    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<Application> updateApplication(@PathVariable Long id, @RequestParam Application.Status status) {
        return ResponseEntity.ok(applicationService.updateApplication(id, status));
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