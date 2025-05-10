package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.dto.InternshipResponse;
import com.example.internship_service.model.Internship;
import com.example.internship_service.services.InternshipService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;
    private final RestTemplate restTemplate;

    public InternshipController(InternshipService internshipService, RestTemplate restTemplate) {
        this.internshipService = internshipService;
        this.restTemplate = restTemplate;
    }

    /*@GetMapping
    @JwtValidation(requiredRoles = {"student","admin"})
    public ResponseEntity<List<Internship>> getAllInternships() {
        return ResponseEntity.ok(internshipService.getAllInternships());
    }*/
    @GetMapping
    public ResponseEntity<Page<InternshipResponse>> getAllInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<Internship> internships = internshipService.getAllInternships(page, size, search);

        Page<InternshipResponse> responsePage = internships.map(internship -> {
            InternshipResponse dto = new InternshipResponse();

            dto.setId(internship.getId());
            dto.setTitle(internship.getTitle());
            dto.setDescription(internship.getDescription());
            dto.setCompanyName(internship.getCompanyName());
            dto.setLocation(internship.getLocation());
            dto.setType(internship.getType().toString());
            dto.setDuration(internship.getDuration());
            dto.setStipend(internship.getStipend());
            dto.setStatus(internship.getStatus().toString());
            dto.setCreatedAt(internship.getCreatedAt().toString());
            dto.setHrId(internship.getHrId());

            try {
                // Replace with the actual URL of your user-service
                String hrUrl = "http://localhost:8765/user-service/hr/public-hr/" + internship.getHrId();
                ResponseEntity<Map> hrResponse = restTemplate.getForEntity(hrUrl, Map.class);

                if (hrResponse.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> hrData = hrResponse.getBody();
                    dto.setHrName((String) hrData.get("name"));
                    dto.setHrCompanyName((String) hrData.get("companyName"));
                }
            } catch (Exception e) {
                // Log the error and skip HR info (optional)
                System.out.println("Failed to fetch HR info for hrId=" + internship.getHrId());
            }

            return dto;
        });

        return ResponseEntity.ok(responsePage);
    }
    @GetMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr","student","admin"})
    public ResponseEntity<Internship> getOneInternship(@PathVariable Long id) {
        return ResponseEntity.ok(internshipService.getOneInternship(id));
    }
    @GetMapping("/active") // done
    @JwtValidation(requiredRoles = {"hr","student","admin"})
    public ResponseEntity<List<Internship>> getActiveInternships() {
        return ResponseEntity.ok(internshipService.getActiveInternships());
    }

    @GetMapping("/hrId/{hrId}")
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<?> getIdsByHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(internshipService.getInternshipsByHrId(hrId));
    }

    @GetMapping("/hr/{hrId}")
    @JwtValidation(requiredRoles = {"hr"})
    public ResponseEntity<Page<Internship>> getByHr(
            @PathVariable Long hrId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String search) { // Add the search parameter

        Pageable pageable = PageRequest.of(page, size,
                direction != null && sortBy != null
                        ? Sort.by(Sort.Direction.fromString(direction), sortBy)
                        : Sort.unsorted());

        return ResponseEntity.ok(internshipService.getInternshipsByHr(hrId, pageable, search));
    }
    @PostMapping // done
    @JwtValidation(requiredRoles = {"hr", "admin"})
    public ResponseEntity<?> createInternship(@RequestBody Internship internship, @RequestHeader("Authorization") String token) {
        try {
            Internship created = internshipService.createInternship(internship, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}") // done
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<?> updateInternship(@PathVariable Long id, @RequestBody Internship internship) {
        try {
            boolean isUpdated = internshipService.updateInternship(id, internship);
            if (isUpdated) {
                return ResponseEntity.ok(internshipService.getOneInternship(id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/count")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<?> getCount() {
        try {
            Long count = internshipService.getInternshipCount();
            return ResponseEntity.ok(count); // Return the count with a 200 OK status

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<String> deleteInternship(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. Get all applications for this internship
            String getHrIdsUrl = "http://localhost:8765/internship-service/applications/internshipIds/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(
                    getHrIdsUrl,
                    HttpMethod.GET,
                    requestEntity,
                    List.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Long> applicationIds = response.getBody();

                // 2. Delete all applications if they exist
                if (!applicationIds.isEmpty()) {
                    String bulkDeleteUrl = "http://localhost:8765/internship-service/applications/bulk";

                    HttpHeaders deleteHeaders = new HttpHeaders();
                    deleteHeaders.setContentType(MediaType.APPLICATION_JSON);
                    deleteHeaders.set("Authorization", authHeader);  // Add auth header here too

                    HttpEntity<List<Long>> deleteRequestEntity = new HttpEntity<>(applicationIds, deleteHeaders);

                    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                            bulkDeleteUrl,
                            HttpMethod.DELETE,
                            deleteRequestEntity,
                            Void.class
                    );

                    if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to delete related applications");
                    }
                }
            }

            // 3. Now delete the internship
            boolean isDeleted = internshipService.deleteInternship(id);

            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Internship deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Internship not found with ID: " + id);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting internship: " + e.getMessage());
        }
    }
    @DeleteMapping("/bulk")
    public ResponseEntity<String> deleteInternshipInBulk(
            @RequestBody List<Long> internshipIds,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. Get all applications associated with these internships
            String getApplicationsUrl = "http://localhost:8765/internship-service/applications/internshipIds/bulk";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader);

            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(internshipIds, headers);

            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    getApplicationsUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<Long>>() {}
            );

            List<Long> applicationIds = response.getBody();

            // 2. Delete all applications (if any exist)
            if (applicationIds != null && !applicationIds.isEmpty()) {
                String bulkDeleteUrl = "http://localhost:8765/internship-service/applications/bulk";

                HttpEntity<List<Long>> deleteRequest = new HttpEntity<>(applicationIds, headers);

                restTemplate.exchange(
                        bulkDeleteUrl,
                        HttpMethod.DELETE,
                        deleteRequest,
                        Void.class
                );
            }

            // 3. Finally, delete the internships
            internshipService.deleteAllByIds(internshipIds);

            return ResponseEntity.ok("Deleted " + internshipIds.size() + " internships and their applications");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete internships: " + e.getMessage());
        }
    }
    @PostMapping("/hr/bulk") // New endpoint
    public ResponseEntity<List<Long>> getInternshipIdsByHrIds(@RequestBody List<Long> hrIds) {
        List<Long> internshipIds = internshipService.findIdsByHrIds(hrIds);
        return ResponseEntity.ok(internshipIds);
    }
}
/*
Post create intern ----> create
get getAll --> get all (student)
get getwihthr --> get all intern with hr id == {id} (hr)
get oneIntern ---> get one intern (student)
Put Update --> update
Delete delete ---> delete
*/