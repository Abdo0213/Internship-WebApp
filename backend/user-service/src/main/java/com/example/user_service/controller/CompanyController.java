package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.CompanyDto;
import com.example.user_service.model.Company;
import com.example.user_service.model.Hr;
import com.example.user_service.services.CompanyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final RestTemplate restTemplate;


    @Autowired
    public CompanyController(CompanyService companyService, RestTemplate restTemplate) {
        this.companyService = companyService;
        this.restTemplate = restTemplate;
    }

    @GetMapping // done
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}") // done
    public Company getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    @PostMapping // done
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto companyDto) {
        try {
            Company company = companyService.addCompany(companyDto);

            if (company != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(company);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User Added failed");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}") // done
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        try {
            Company company = companyService.updateCompany(id, companyDto);
            if (company != null) {
                return ResponseEntity.ok(companyService.getCompanyById(id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. Fetch all HRs for this company
            String getHrIdsUrl = "http://localhost:8765/user-service/hr/company/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    getHrIdsUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Long>>() {}
            );
            // 2. Delete HRs in bulk (if any exist)
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Long> hrIds = response.getBody();
                if (!hrIds.isEmpty()) {
                    System.out.println(hrIds);
                    String bulkDeleteUrl = "http://localhost:8765/user-service/hr/bulk";  // Fixed URL
                    HttpEntity<List<Long>> deleteRequest = new HttpEntity<>(hrIds, headers);
                    restTemplate.exchange(
                            bulkDeleteUrl,
                            HttpMethod.DELETE,
                            deleteRequest,
                            Void.class
                    );
                }
            }

            // 3. Delete the company
            boolean isDeleted = companyService.deleteCompany(id);
            return isDeleted
                    ? ResponseEntity.ok("Company and HRs deleted")
                    : ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Deletion failed: " + e.getMessage());
        }
    }
    @GetMapping("/count")
    @JwtValidation(requiredRoles = {"hr","admin"})
    public ResponseEntity<?> getCount() {
        try {
            Long count = companyService.getCompanyCount();
            return ResponseEntity.ok(count); // Return the count with a 200 OK status

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
