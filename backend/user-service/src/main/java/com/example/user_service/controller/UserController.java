package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.model.User;
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/hr")
    public ResponseEntity<String> getUsers() throws JsonProcessingException {
        // here is ----> 6
        return null;
    }

    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<String> getOneUser(@PathVariable Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);
        return ResponseEntity.ok("very nice");
    }

    @PostMapping("/hr")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> addUser(@RequestBody RegisterRequest registerRequest) throws JsonProcessingException {
        /// ------> 5
        Boolean isAdded =  userService.AddUser(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail(), registerRequest.getCompany());
        return null;
    }
    @PutMapping("/hr/{id}")
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<String> updateUser(@PathVariable Long id) throws JsonProcessingException {
        /// update user with the id ----> 4
        return null;
    }
    @DeleteMapping("/hr/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws JsonProcessingException {
        Boolean isDeleted = userService.DeleteUser(id);
        return null;
    }
}
/*
get all users
get users (id) --> get one user
post users --> create user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */