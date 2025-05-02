package com.example.user_service.controller;

import com.example.user_service.config.JwtUtil;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.services.AuthService;
import com.example.user_service.annotation.JwtValidation;  // Make sure you import your custom annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String username = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );
        return ResponseEntity.ok("User registered");
    }
}
