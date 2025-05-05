package com.example.user_service.controller;

import com.example.user_service.dto.CompanyDto;
import com.example.user_service.model.Company;
import com.example.user_service.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public Company getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }



    @PostMapping
    public void addCompany(@RequestBody CompanyDto companyDto) {
        companyService.addCompany(companyDto);
    }

    @PutMapping("/{id}")
    public boolean updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return companyService.updateCompany(id, companyDto);
    }

    @DeleteMapping("/{id}")
    public boolean deleteCompany(@PathVariable Long id) {
        return companyService.deleteCompany(id);
    }
}
