package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.model.User;
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> getOneUser(@PathVariable Long id) throws JsonProcessingException {
        try {
            User user = userService.getUserById(id);  // Returns null if not found

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with ID: " + id);
            }

            // Convert user to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String userJson = mapper.writeValueAsString(user);
            return ResponseEntity.ok(userJson);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving user: " + e.getMessage());
        }
    }

    @PostMapping
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> addUser(@RequestBody RegisterRequest registerRequest) throws JsonProcessingException {
        try {
            Boolean isAdded = userService.AddUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getfName(),
                    registerRequest.getRoleName()
            );

            if (isAdded) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("User Added successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User Added failed");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        try {
            boolean isUpdated = userService.UpdateUser(
                    id,
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getfName(),
                    request.getRoleName()
            );

            if (isUpdated) {
                return ResponseEntity.ok("User updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws JsonProcessingException {
        try {
            boolean isDeleted = userService.DeleteUser(id);

            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("User deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with ID: " + id);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
/*
get all users
get users (id) --> get one user
post users --> create user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */