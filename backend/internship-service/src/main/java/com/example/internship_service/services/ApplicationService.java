package com.example.internship_service.services;

import com.example.internship_service.model.Application;
import com.example.internship_service.model.Internship;
import com.example.internship_service.repository.ApplicationRepository;
import com.example.internship_service.repository.InternshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              InternshipRepository internshipRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
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
    public Application createApplication(Long studentId, Long internshipId) {
        if (applicationRepository.existsByStudentIdAndInternshipId(studentId, internshipId)) {
            throw new IllegalStateException("Application already exists");
        }

        // Create and save new application
        Application application = new Application(studentId, internshipId);
        return applicationRepository.save(application);
    }

    // Update application status (e.g., PENDING → ACCEPTED/REJECTED)
    public Application updateApplication(Long id, Application.Status newStatus) {
        Application application = getOneApplication(id);
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }
}
