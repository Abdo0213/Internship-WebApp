package com.example.internship_service.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    // Student endpoints
    @GetMapping("/students/exists/{studentId}")
    ResponseEntity<Void> checkStudentExists(
            @PathVariable Long studentId,  // Explicit path variable name
            @RequestHeader("Authorization") String token
    );

    // HR endpoints
    @GetMapping("/hr/exists/{hrId}")
    ResponseEntity<Void> checkHrExists(
            @PathVariable Long hrId,
            @RequestHeader("Authorization") String token
    );
}