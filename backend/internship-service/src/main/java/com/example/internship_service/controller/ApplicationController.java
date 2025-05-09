package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.dto.ApplicationRequest;
import com.example.internship_service.dto.ApplicationResponse;
import com.example.internship_service.dto.InternshipResponse;
import com.example.internship_service.dto.UpdatedApplicationRequest;
import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.stream.Collectors;



import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final RestTemplate restTemplate;


    public ApplicationController(ApplicationService applicationService, RestTemplate restTemplate) {
        this.applicationService = applicationService;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    @JwtValidation(requiredRoles = {"student"}) // done
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
    @GetMapping("/student/{id}") // done
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<List<Application>> getStudentApplications(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getStudentApplications(id));
    }

    // get one application student or hr
    @GetMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr","student"})
    public ResponseEntity<Application> getOneApplication(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getOneApplication(id));
    }

    @GetMapping("/hr/{hrId}") // done
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<List<Application>> getApplicationsByHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(applicationService.getApplicationsByHr(hrId));
    }
    @GetMapping("/internship/{internshipId}") // done
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByInternship(@PathVariable Long internshipId) {
        List<Application> applications = applicationService.getApplicationsByInternship(internshipId);

        List<ApplicationResponse> applicationResponses = applications.stream()
                .map(application -> {
                    ApplicationResponse dto = new ApplicationResponse();
                    dto.setId(application.getId());
                    dto.setInternship(application.getInternship());
                    dto.setStudentId(application.getStudentId());
                    dto.setStatus(application.getStatus().toString());
                    dto.setAppliedAt(application.getAppliedAt());

                    try {
                        String studentUrl = "http://localhost:8765/user-service/students/public-student/" + application.getStudentId();
                        ResponseEntity<Map> response = restTemplate.getForEntity(studentUrl, Map.class);

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            Map<String, Object> studentData = response.getBody();
                            dto.setStudentName(String.valueOf(studentData.get("name")));
                            dto.setStudentCollege(String.valueOf(studentData.get("college")));
                            dto.setStudentCV(String.valueOf(studentData.get("cv")));
                            dto.setStudentFaculty(String.valueOf(studentData.get("faculty")));
                            dto.setStudentGrade(String.valueOf(studentData.get("grade")));
                        }
                    } catch (Exception e) {
                        // Silently handle error (as per your requirement)
                        dto.setStudentName("Unknown");
                        dto.setStudentCollege("Unknown");
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(applicationResponses);
    }

    @PutMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<Application> updateApplication(
            @PathVariable Long id,
            @RequestBody UpdatedApplicationRequest dto) {
        return ResponseEntity.ok(applicationService.updateApplication(id, dto.getStatus()));
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