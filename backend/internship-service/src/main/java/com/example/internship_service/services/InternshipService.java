package com.example.internship_service.services;

import com.example.internship_service.config.UserServiceClient;
import com.example.internship_service.model.Internship;
import com.example.internship_service.repository.InternshipRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final UserServiceClient userServiceClient;

    public InternshipService(InternshipRepository internshipRepository, UserServiceClient hrServiceClient) {
        this.internshipRepository = internshipRepository;
        this.userServiceClient = hrServiceClient;
    }

    // Create new internship (auto-sets 1-hour expiry)
    public Internship createInternship(Internship internship, String authToken) {
        try {
            ResponseEntity<Void> response = userServiceClient.checkHrExists(
                    internship.getHrId(),
                    authToken);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HR not found with ID: " + internship.getHrId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("HR not found with ID: " + internship.getHrId());
        }
        return internshipRepository.save(internship);
    }
    // Get active internships
    public List<Internship> getActiveInternships() {
        return internshipRepository.findActiveInternships(LocalDateTime.now());
    }
    public List<Internship> getAllInternships() {
        return internshipRepository.findAll();
    }
    public Internship getOneInternship(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found with ID: " + id));
    }
    // Get internships by HR
    public List<Internship> getInternshipsByHr(Long hrId) {
        return internshipRepository.findByHrId(hrId);
    }
    @Transactional
    public boolean updateInternship(Long id, Internship updatedInternship) {
        // 1. Find existing internship
        Internship existingInternship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found with ID: " + id));

        boolean wasUpdated = false;

        // 2. Check and update each field
        if (updatedInternship.getTitle() != null && !updatedInternship.getTitle().equals(existingInternship.getTitle())) {
            existingInternship.setTitle(updatedInternship.getTitle());
            wasUpdated = true;
        }

        if (updatedInternship.getDescription() != null && !updatedInternship.getDescription().equals(existingInternship.getDescription())) {
            existingInternship.setDescription(updatedInternship.getDescription());
            wasUpdated = true;
        }

        // 3. Update other fields similarly
        if (updatedInternship.getCompanyName() != null) {
            existingInternship.setCompanyName(updatedInternship.getCompanyName());
            wasUpdated = true;
        }

        if (updatedInternship.getLocation() != null) {
            existingInternship.setLocation(updatedInternship.getLocation());
            wasUpdated = true;
        }

        if (updatedInternship.getType() != null) {
            existingInternship.setType(updatedInternship.getType());
            wasUpdated = true;
        }
        if (updatedInternship.getStipend() != null) {
            existingInternship.setStipend(updatedInternship.getStipend());
            wasUpdated = true;
        }
        if (updatedInternship.getStatus() != null) {
            existingInternship.setStatus(updatedInternship.getStatus());
            wasUpdated = true;
        }
        if (updatedInternship.getDuration() != null) {
            existingInternship.setDuration(updatedInternship.getDuration());
            wasUpdated = true;
        }

        // 4. Save only if changes were made
        if (wasUpdated) {
            internshipRepository.save(existingInternship);
        }

        return wasUpdated;
    }
    public boolean deleteInternship(Long id) {
        if(internshipRepository.existsById(id)){
            internshipRepository.deleteById(id);
            return true;
        }
        return false;
    }
    // Scheduled expiration check (runs every minute)
    @Scheduled(fixedRate = 60000)
    public void checkExpiredInternships() {
        internshipRepository.expireOldInternships(LocalDateTime.now());
    }
}
