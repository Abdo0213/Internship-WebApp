package com.example.internship_service.services;

import com.example.internship_service.config.UserServiceClient;
import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.repository.ApplicationRepository;
import com.example.internship_service.repository.InternshipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserServiceClient userServiceClient;


    public ApplicationService(ApplicationRepository applicationRepository, InternshipRepository internshipRepository, UserServiceClient userServiceClient) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userServiceClient = userServiceClient;
    }
    // Get all applications for a specific student
    public List<Application> getStudentApplications(Long studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    // Get single application by ID
    public Application getOneApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));
    }


    // Get all applications for internships posted by specific HR
    public List<Application> getApplicationsByHr(Long hrId) {
        // First get all internships by this HR
        List<Long> internshipIds = internshipRepository.findByHrId(hrId)
                .stream()
                .map(Internship::getId)
                .toList();

        // Then get applications for these internships
        return applicationRepository.findByInternshipIdIn(internshipIds);
    }
    public Application createApplication(Long studentId, Long internshipId, String authToken) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        // 2. Verify student exists (remote call)
        ResponseEntity<Void> response = userServiceClient.checkStudentExists(
                studentId,
                authToken
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Student not found");
        }

        // 3. Check for duplicate application
        if (applicationRepository.existsByStudentIdAndInternshipId(
                studentId,
                internshipId)) {
            throw new RuntimeException("Already applied to this internship");
        }

        // Create and save new application
        Application application = new Application(studentId, internship);
        return applicationRepository.save(application);
    }

    // Update application status (e.g., PENDING → ACCEPTED/REJECTED)
    public Application updateApplication(Long id, Application.Status newStatus) {
        Application application = getOneApplication(id);
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }
}
