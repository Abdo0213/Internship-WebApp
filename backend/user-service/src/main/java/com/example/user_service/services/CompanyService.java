package com.example.user_service.services;

import com.example.user_service.dto.CompanyDto;
import com.example.user_service.model.Company;
import com.example.user_service.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;


    public CompanyService(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
    }

    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public void addCompany(CompanyDto dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setIndustry(dto.getIndustry());
        company.setLocation(dto.getLocation());

        companyRepository.save(company);
    }

    public boolean updateCompany(Long id, CompanyDto updatedDto) {
        Optional<Company> existingCompanyOpt = companyRepository.findById(id);

        if (existingCompanyOpt.isEmpty()) {
            return false;
        }

        Company existingCompany = existingCompanyOpt.get();
        boolean wasUpdated = false;

        // Update company-specific fields
        if (updatedDto.getName() != null && !updatedDto.getName().equals(existingCompany.getName())) {
            existingCompany.setName(updatedDto.getName());
            wasUpdated = true;
        }

        if (updatedDto.getIndustry() != null && !updatedDto.getIndustry().equals(existingCompany.getIndustry())) {
            existingCompany.setIndustry(updatedDto.getIndustry());
            wasUpdated = true;
        }

        if (updatedDto.getLocation() != null && !updatedDto.getLocation().equals(existingCompany.getLocation())) {
            existingCompany.setLocation(updatedDto.getLocation());
            wasUpdated = true;
        }

        if (wasUpdated) {
            companyRepository.save(existingCompany);
        }

        return wasUpdated;
    }

    public Boolean deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
