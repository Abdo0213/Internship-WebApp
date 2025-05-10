package com.example.user_service.repository;

import com.example.user_service.model.Hr;
import com.example.user_service.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HrRepository extends JpaRepository<Hr, Long> {
    Optional<Hr> findByUserUsername(String username);
    Optional<Hr> findByUserEmail(String email);

    @Query("SELECT h.id FROM Hr h WHERE h.company.id = :companyId")
    List<Long> findByCompanyId(@Param("companyId") Long companyId);
    @Modifying
    @Query("DELETE FROM Hr h WHERE h.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);

}
