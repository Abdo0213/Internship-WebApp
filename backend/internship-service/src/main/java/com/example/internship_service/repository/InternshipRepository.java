package com.example.internship_service.repository;

import com.example.internship_service.model.Internship;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    // Active internships (not expired)
    @Query("SELECT i FROM Internship i WHERE i.status = 'ACTIVE'")
    List<Internship> findActiveInternships(LocalDateTime now);
    Page<Internship> findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
            String title, String companyName, Pageable pageable);
    Page<Internship> findByHrIdAndTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            Long hrId, String title, String companyName, String description, Pageable pageable
    );
    Page<Internship> findByHrId(Long hrId, Pageable pageable);
    // Find by HR
    List<Internship> findByHrId(Long hrId);
    @Query("SELECT h.id FROM Internship h WHERE h.hrId = :hrId")
    List<Long> findByHrId2(@Param("hrId") Long hrId);
    @Query("SELECT i.id FROM Internship i WHERE i.hrId IN :hrIds")
    List<Long> findIdsByHrIdIn(@Param("hrIds") List<Long> hrIds);

}
