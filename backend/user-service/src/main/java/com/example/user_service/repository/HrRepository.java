package com.example.user_service.repository;

import com.example.user_service.model.Hr;
import com.example.user_service.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrRepository extends JpaRepository<Hr, Long> {
    Optional<Hr> findByUserUsername(String username);
    Optional<Hr> findByUserEmail(String email);

}
