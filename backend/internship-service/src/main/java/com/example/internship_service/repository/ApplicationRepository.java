package com.example.internship_service.repository;

import com.example.internship_service.model.Application;
import com.example.internship_service.model.Application.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByInternshipId(Long internshipId);
    List<Application> findByInternshipIdIn(List<Long> internshipIds);
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);
    @Query("SELECT h.id FROM Application h WHERE h.internship.id = :internshipId")
    List<Long> findByInternshipId2(@Param("internshipId") Long internshipId);
    @Query("SELECT a.id FROM Application a WHERE a.internship.id IN :internshipIds")
    List<Long> findIdsByInternshipIdIn(@Param("internshipIds") List<Long> internshipIds);
}