package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.dto.ApplicationRequest;
import com.example.internship_service.dto.ApplicationResponse;
import com.example.internship_service.dto.InternshipResponse;
import com.example.internship_service.dto.UpdatedApplicationRequest;
import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.model.Notification;
import com.example.internship_service.services.ApplicationService;
import com.example.internship_service.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.stream.Collectors;



import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final RestTemplate restTemplate;
    @Autowired
    private NotificationService notificationService;

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

    @GetMapping("/internshipIds/{internshipId}")
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<?> getIdsByInternship(@PathVariable Long internshipId) {
        return ResponseEntity.ok(applicationService.getApplicationsIdByInternship(internshipId));
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

        // Update the application
        Application updatedApplication = applicationService.updateApplication(id, dto.getStatus());
        // Only create notification if status actually changed
            Notification notification = new Notification();
            notification.setStudentId(updatedApplication.getStudentId());
            notification.setInternshipTitle(updatedApplication.getInternship().getTitle());
            notification.setApplication(updatedApplication);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationService.createNotification(notification);

        return ResponseEntity.ok(updatedApplication);
    }
    @DeleteMapping("/bulk")
    public ResponseEntity<String> deleteApplicationsInBulk(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody List<Long> applicationIds) {
        try {
            // First delete associated notifications
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader);
            HttpEntity<List<Long>> request = new HttpEntity<>(applicationIds, headers);

            ResponseEntity<Void> notificationResponse = restTemplate.exchange(
                    "http://localhost:8765/internship-service/notifications/bulk",
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );

            // Only delete applications if notification deletion was successful
            if (notificationResponse.getStatusCode().is2xxSuccessful()) {
                applicationService.deleteAllByIds(applicationIds);
                return ResponseEntity.noContent().build();
            } else {
                // Notification deletion failed
                return ResponseEntity.status(notificationResponse.getStatusCode())
                        .body("Failed to delete notifications");
            }

        } catch (RestClientException e) {
            // Return error response without deleting applications
            return ResponseEntity.internalServerError()
                    .body("Failed to communicate with notification service");
        }
    }
    @PostMapping("/internshipIds/bulk")
    public ResponseEntity<List<Long>> getApplicationIdsByInternshipIds(@RequestBody List<Long> internshipIds) {
        List<Long> applicationIds = applicationService.findIdsByInternshipIds(internshipIds);
        return ResponseEntity.ok(applicationIds);
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