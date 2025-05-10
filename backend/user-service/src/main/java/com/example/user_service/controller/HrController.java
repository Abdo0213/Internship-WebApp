package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.HrDto;
import com.example.user_service.model.Hr;
import com.example.user_service.model.User;
import com.example.user_service.services.HrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hr")
public class HrController {
    private final HrService hrService;
    private final RestTemplate restTemplate;


    public HrController(HrService hrService, RestTemplate restTemplate) {
        this.hrService = hrService;
        this.restTemplate = restTemplate;
    }
    @GetMapping // done
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
    @GetMapping("/company/{companyId}") // done
    public ResponseEntity<?> getHrByCompanyId(@PathVariable Long companyId) throws JsonProcessingException {
        try {
            List<Long> hrs = hrService.getHrByCompanyId(companyId);

            if (hrs.isEmpty()) {
                return null;
            }
            return ResponseEntity.ok(hrs);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    @PostMapping // done
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<?> addHr(@RequestBody HrDto registerRequest) throws JsonProcessingException {
        try {
            Hr hr = hrService.addHr(registerRequest);

            if (hr != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(hr);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User Added failed");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @GetMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr","admin"})
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
    @GetMapping("/public-hr/{id}") // done
    public ResponseEntity<?> getPublicInfoHr(@PathVariable Long id) {
        try {
            Hr hr = hrService.getHrById(id);  // Returns null if not found

            if (hr == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "HR not found with ID: " + id));
            }

            // Create a response DTO with public fields only
            Map<String, Object> publicHrInfo = new HashMap<>();
            publicHrInfo.put("id", hr.getId());
            publicHrInfo.put("name", hr.getUser().getFname());
            publicHrInfo.put("companyName", hr.getCompany().getName());

            return ResponseEntity.ok(publicHrInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving HR: " + e.getMessage()));
        }
    }
    @GetMapping("/exists/{id}") // done
    @JwtValidation(requiredRoles = {"hr","admin"}) // Adjust roles as needed
    public ResponseEntity<Void> checkHrExists(@PathVariable Long id) {
        return hrService.hrExists(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<String> updateHr(@PathVariable Long id, @RequestBody User request) {
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
    public ResponseEntity<String> deleteHr(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {  // Add authHeader parameter
        try {
            // 1. Get all internship IDs for this HR
            String getHrIdsUrl = "http://localhost:8765/internship-service/internships/hrId/" + id;  // Use dynamic URL

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader);  // Pass JWT token

            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    getHrIdsUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Long>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Long> internshipIds = response.getBody();

                // 2. Bulk delete internships
                if (!internshipIds.isEmpty()) {
                    String bulkDeleteUrl ="http://localhost:8765/internship-service/internships/bulk";

                    HttpEntity<List<Long>> requestEntity = new HttpEntity<>(internshipIds, headers);
                    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                            bulkDeleteUrl,
                            HttpMethod.DELETE,
                            requestEntity,
                            Void.class
                    );

                    if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                        return ResponseEntity.internalServerError()
                                .body("Failed to delete related internships");
                    }
                }
            }

            // 3. Delete the HR
            boolean isDeleted = hrService.DeleteHr(id);
            if (isDeleted) {
                return ResponseEntity.ok("HR deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("HR not found with ID: " + id);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error deleting HR: " + e.getMessage());
        }
    }
    @DeleteMapping("/bulk")
    @JwtValidation(requiredRoles = {"admin"}) // Optional: Add role restriction
    public ResponseEntity<String> deleteHrInBulk(
            @RequestBody List<Long> hrIds,
            @RequestHeader("Authorization") String authHeader) { // For internal API calls

        try {
            // 1. Get all internship IDs for these HRs
            String getInternshipsUrl = "http://localhost:8765/internship-service/internships/hr/bulk"; // Endpoint to fetch internships by HR IDs

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader); // Forward JWT token

            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(hrIds, headers);

            // Fetch internships linked to HRs
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    getInternshipsUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<Long>>() {}
            );

            List<Long> internshipIds = response.getBody();

            // 2. Delete internships in bulk (if any exist)
            if (internshipIds != null && !internshipIds.isEmpty()) {
                String bulkDeleteInternshipsUrl = "http://localhost:8765/internship-service/internships/bulk";

                HttpEntity<List<Long>> deleteRequest = new HttpEntity<>(internshipIds, headers);
                restTemplate.exchange(
                        bulkDeleteInternshipsUrl,
                        HttpMethod.DELETE,
                        deleteRequest,
                        Void.class
                );
            }

            // 3. Finally, delete the HRs
            hrService.deleteAllByIds(hrIds);

            return ResponseEntity.ok(
                    "Deleted " + hrIds.size() + " HRs and " +
                            (internshipIds != null ? internshipIds.size() : 0) + " linked internships."
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete HRs: " + e.getMessage());
        }
    }
    @GetMapping("/count")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<?> getCount() {
        try {
            Long count = hrService.getHrCount();
            return ResponseEntity.ok(count); // Return the count with a 200 OK status

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
