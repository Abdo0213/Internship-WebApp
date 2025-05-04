package com.example.internship_service.services;

import com.example.internship_service.model.Application;
import com.example.internship_service.repository.ApplicationRepository;
import com.example.internship_service.repository.InternshipRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application createApplication(Long studentId, Long internshipId) {
        if (applicationRepository.existsByStudentIdAndInternshipId(studentId, internshipId)) {
            throw new IllegalStateException("Application already exists");
        }
        return applicationRepository.save(new Application(studentId, internshipId));
    }

    public Application updateApplicationStatus(Long id, Application.Status status) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        return applicationRepository.save(app);
    }
}
