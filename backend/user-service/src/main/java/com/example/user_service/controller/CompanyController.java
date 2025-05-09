package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.CompanyDto;
import com.example.user_service.model.Company;
import com.example.user_service.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
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
    public void addCompany(@RequestBody CompanyDto companyDto) {
        companyService.addCompany(companyDto);
    }

    @PutMapping("/{id}") // done
    public boolean updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return companyService.updateCompany(id, companyDto);
    }

    @DeleteMapping("/{id}") // done
    public boolean deleteCompany(@PathVariable Long id) {
        return companyService.deleteCompany(id);
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
