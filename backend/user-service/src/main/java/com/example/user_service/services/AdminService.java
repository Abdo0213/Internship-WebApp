package com.example.user_service.services;

import com.example.user_service.dto.StudentDto;
import com.example.user_service.model.Admin;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.repository.AdminRepository;
import com.example.user_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public AdminService(AdminRepository adminRepository, UserService userService) {
        this.adminRepository = adminRepository;
        this.userService = userService;
    }
    public Admin getOneAdmin(Long id) {

        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public boolean updateAdmin(Long id, User updatedAdmin) {
        Optional<Admin> existingAdminOpt = adminRepository.findById(id);

        if (existingAdminOpt.isEmpty()) {
            return false;
        }

        Admin existingAdmin = existingAdminOpt.get();
        boolean wasUpdated = false;

        // Check and update each field

        if (updatedAdmin.getFname() != null && !updatedAdmin.getFname().equals(existingAdmin.getName())) {
            existingAdmin.setName(updatedAdmin.getFname());
            wasUpdated = true;
        }
        User user = existingAdmin.getUser();
        System.out.println(updatedAdmin.getFname() + "-------------------------------");
        // Handle password separately
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
            wasUpdated = true;
        }
        if (updatedAdmin.getEmail() != null && !updatedAdmin.getEmail().isEmpty()) {
            user.setEmail(updatedAdmin.getEmail());
            wasUpdated = true;
        }
        if (updatedAdmin.getUsername() != null && !updatedAdmin.getUsername().isEmpty()) {
            user.setUsername(updatedAdmin.getUsername());
            wasUpdated = true;
        }
        if (updatedAdmin.getFname() != null && !updatedAdmin.getFname().equals(user.getFname())) {
            user.setFname(updatedAdmin.getFname());
            wasUpdated = true;
        }
        if (wasUpdated) {
            userService.UpdateUser(user.getId(), user.getUsername(), updatedAdmin.getPassword(), user.getEmail(), user.getFname(), user.getRole().getName());
            adminRepository.save(existingAdmin);
        }

        return wasUpdated;
    }
}
