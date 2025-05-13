package com.example.internship_service.services;

import com.example.internship_service.config.UserServiceClient;
import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.repository.ApplicationRepository;
import com.example.internship_service.repository.InternshipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


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
    public List<Application> getApplicationsByInternship(Long internshipId) {
        return applicationRepository.findByInternshipId(internshipId);
    }
    public List<Long> getApplicationsIdByInternship(Long internshipId) {
        return applicationRepository.findByInternshipId2(internshipId);
    }
    public Application createApplication(Long studentId, Long internshipId, String authToken) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));

        ResponseEntity<Void> response = userServiceClient.checkStudentExists(studentId, authToken);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }

        boolean alreadyApplied = applicationRepository.existsByStudentIdAndInternshipId(studentId, internshipId);
        if (alreadyApplied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already applied to this internship");
        }

        Application application = new Application(studentId, internship);
        application.setStatus(Application.Status.PENDING); // explicitly set, though default exists

        return applicationRepository.save(application);
    }
    // Update application status (e.g., PENDING → ACCEPTED/REJECTED)
    public Application updateApplication(Long id, Application.Status status) {
        Application application = getOneApplication(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }
    public void deleteAllByIds(List<Long> ids) {
        List<Application> applications = applicationRepository.findAllById(ids);
        applicationRepository.deleteAll(applications);
    }
    public List<Long> findIdsByInternshipIds(List<Long> internshipIds) {
        return applicationRepository.findIdsByInternshipIdIn(internshipIds);
    }
}
