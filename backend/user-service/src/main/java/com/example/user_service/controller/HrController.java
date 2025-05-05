package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.HrDto;
import com.example.user_service.model.Hr;
import com.example.user_service.services.HrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hr")
public class HrController {
    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }
    @GetMapping
    public ResponseEntity<String> getAllHr() throws JsonProcessingException {
        try {
            List<Hr> hrs = hrService.getAllHr();

            if (hrs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No users found");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String usersJson = objectMapper.writeValueAsString(hrs);
            return ResponseEntity.ok(usersJson);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing user data");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving users: " + e.getMessage());
        }
    }
    @PostMapping
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> addHr(@RequestBody HrDto registerRequest) throws JsonProcessingException {
        try {
            Boolean isAdded = hrService.addHr(
                    registerRequest
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
    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<String> getOneHr(@PathVariable Long id) throws JsonProcessingException {
        try {
            Hr hr = hrService.getHrById(id);  // Returns null if not found

            if (hr == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("hr not found with ID: " + id);
            }

            // Convert user to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String userJson = mapper.writeValueAsString(hr);
            return ResponseEntity.ok(userJson);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving hr: " + e.getMessage());
        }
    }
    @GetMapping("/exists/{id}")
    @JwtValidation(requiredRoles = {"hr"}) // Adjust roles as needed
    public ResponseEntity<Void> checkHrExists(@PathVariable Long id) {
        return hrService.hrExists(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<String> updateHr(@PathVariable Long id, @RequestBody HrDto request) {
        try {
            boolean isUpdated = hrService.updateHr(id, request);
            if (isUpdated) {
                return ResponseEntity.ok("hr updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin", "hr"})
    public ResponseEntity<String> deleteHr(@PathVariable Long id) throws JsonProcessingException {
        try {
            boolean isDeleted = hrService.DeleteHr(id);

            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("hr deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("hr not found with ID: " + id);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
