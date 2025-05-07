package com.example.internship_service.repository;

import com.example.internship_service.model.Internship;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    // Active internships (not expired)
    @Query("SELECT i FROM Internship i WHERE i.status = 'ACTIVE' AND i.expiresAt > ?1")
    List<Internship> findActiveInternships(LocalDateTime now);
    Page<Internship> findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
            String title, String companyName, Pageable pageable);
    Page<Internship> findByHrId(Long hrId, Pageable pageable);
    // Expiration batch update
    @Transactional
    @Modifying
    @Query("UPDATE Internship i SET i.status = 'EXPIRED' WHERE i.expiresAt < ?1 AND i.status = 'ACTIVE'")
    void expireOldInternships(LocalDateTime now);

    // Find by HR
    List<Internship> findByHrId(Long hrId);
}
