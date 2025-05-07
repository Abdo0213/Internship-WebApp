package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.StudentDto;
import com.example.user_service.model.Admin;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.services.AdminService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> getOneStudent(@PathVariable Long id) throws JsonProcessingException {
        try {
            Admin admin = adminService.getOneAdmin(id);  // Returns null if not found

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Admin not found with ID: " + id);
            }

            // Convert user to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String userJson = mapper.writeValueAsString(admin);
            return ResponseEntity.ok(userJson);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving admin: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody User request) {
        try {
            boolean isUpdated = adminService.updateAdmin(id, request);
            if (isUpdated) {
                return ResponseEntity.ok("Student updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
