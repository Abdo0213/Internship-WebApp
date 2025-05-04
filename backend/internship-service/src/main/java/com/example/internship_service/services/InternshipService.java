package com.example.internship_service.services;

import com.example.internship_service.model.Internship;
import com.example.internship_service.repository.InternshipRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;

    public InternshipService(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    // Create new internship (auto-sets 1-hour expiry)
    public Internship createInternship(Internship internship) {
        return internshipRepository.save(internship);
    }

    // Get active internships
    public List<Internship> getActiveInternships() {
        return internshipRepository.findActiveInternships(LocalDateTime.now());
    }

    // Get internships by HR
    public List<Internship> getInternshipsByHr(Long hrId) {
        return internshipRepository.findByHrId(hrId);
    }

    // Scheduled expiration check (runs every minute)
    @Scheduled(fixedRate = 60000)
    public void checkExpiredInternships() {
        internshipRepository.expireOldInternships(LocalDateTime.now());
    }
}
