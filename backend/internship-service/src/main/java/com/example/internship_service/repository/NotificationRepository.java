package com.example.internship_service.repository;

import com.example.internship_service.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByStudentId(Long studentId, Pageable pageable);

    Optional<Notification> findByIdAndStudentId(Long id, Long studentId);

    Page<Notification> findByStudentIdAndInternshipTitleContainingIgnoreCase(
            Long studentId, String searchTerm, Pageable pageable);
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.application.id IN :applicationIds")
    void deleteAllByApplicationIds(@Param("applicationIds") List<Long> applicationIds);
}