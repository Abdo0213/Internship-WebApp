package com.example.internship_service.repository;

import com.example.internship_service.model.Application;
import com.example.internship_service.model.Application.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByInternshipId(Long internshipId);
    List<Application> findByStatus(Status status);
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);
}