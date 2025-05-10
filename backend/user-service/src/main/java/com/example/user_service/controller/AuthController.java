package com.example.user_service.controller;

import com.example.user_service.config.JwtUtil;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.model.Role;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.services.AuthService;
import com.example.user_service.services.StudentService;
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
// will use this is siisiisisiisis
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) throws JsonProcessingException {
        // authenticate add return role
        Map<String, String> data = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );
        User user = userService.getUserByUsername(request.getUsername());
        String role = user.getRole().getName();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("userId", user.getId());
        response.put("username", data.get("username"));
        response.put("role", role);

        String jsonResponse = new ObjectMapper().writeValueAsString(response);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        System.out.println(request);
        authService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getfName(),
                request.getCv()
        );
        return ResponseEntity.ok("User registered");
    }
}
