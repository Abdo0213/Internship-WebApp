package com.example.user_service.repository;

import com.example.user_service.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserUsername(String username);
    Optional<Admin> findByUserEmail(String email);
}
