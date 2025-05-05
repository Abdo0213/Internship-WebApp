package com.example.user_service.services;

import com.example.user_service.dto.CompanyDto;
import com.example.user_service.model.Company;
import com.example.user_service.model.User;
import com.example.user_service.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    public Company getCompanyByUsername(String username) {
        return companyRepository.findByUserUsername(username)
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
        userService.AddUser(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getfName(), "Company");

        User user = userService.getUserByUsername(dto.getUsername());
        Company company = new Company();
        company.setUser(user);
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

        // Update user info
        User user = existingCompany.getUser();

        if (updatedDto.getPassword() != null && !updatedDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedDto.getPassword()));
            wasUpdated = true;
        }

        if (updatedDto.getEmail() != null && !updatedDto.getEmail().isEmpty()) {
            user.setEmail(updatedDto.getEmail());
            wasUpdated = true;
        }

        if (updatedDto.getUsername() != null && !updatedDto.getUsername().isEmpty()) {
            user.setUsername(updatedDto.getUsername());
            wasUpdated = true;
        }

        if (updatedDto.getfName() != null && !updatedDto.getfName().equals(user.getFname())) {
            user.setFname(updatedDto.getfName());
            wasUpdated = true;
        }

        if (wasUpdated) {
            userService.UpdateUser(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getFname(), user.getRole().getName());
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
