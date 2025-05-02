package com.example.user_service.controller;

import com.example.user_service.config.JwtUtil;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.model.User;
import com.example.user_service.services.AuthService;
import com.example.user_service.annotation.JwtValidation;  // Make sure you import your custom annotation
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) throws JsonProcessingException {
        String username = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );
        String token = jwtUtil.generateToken(username);
        User user = userService.getUserByUsername(request.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("userId", user.getId());
        response.put("username", username);

        String jsonResponse = new ObjectMapper().writeValueAsString(response);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
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
