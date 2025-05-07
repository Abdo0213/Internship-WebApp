package com.example.user_service.services;

import com.example.user_service.dto.HrDto;
import com.example.user_service.model.Company;
import com.example.user_service.model.Hr;
import com.example.user_service.model.Role;
import com.example.user_service.model.User;
import com.example.user_service.repository.CompanyRepository;
import com.example.user_service.repository.HrRepository;
import com.example.user_service.repository.RoleRepository;
import com.example.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HrService {

    private final HrRepository hrRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    public HrService(HrRepository hrRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository, RoleRepository roleRepository) {
        this.hrRepository = hrRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public List<Hr> getAllHr() {
        return hrRepository.findAll();
    }

    public Hr getHrById(Long id) {
        return hrRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean updateHr(Long id, User updatedHr) {
        Optional<Hr> optionalHr = hrRepository.findById(id);
        if (optionalHr.isEmpty()) {
            throw new RuntimeException("HR not found with ID: " + id);
        }

        Hr existingHr = optionalHr.get();
        User user = existingHr.getUser();

        System.out.println(updatedHr.getFname());
        System.out.println("------------************");
        // Update user fields
        if (updatedHr.getUsername() != null) {
            user.setUsername(updatedHr.getUsername());
        }
        if (updatedHr.getEmail() != null) {
            user.setEmail(updatedHr.getEmail());
        }
        if (updatedHr.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updatedHr.getPassword())); // Make sure to hash the password
        }
        if (updatedHr.getFname() != null) {
            user.setFname(updatedHr.getFname());
        }


        try {
            userRepository.save(user);
            hrRepository.save(existingHr);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update HR: " + e.getMessage());
        }
    }
    @Transactional
    public boolean addHr(HrDto hrDto) {
        // 1. Check if user with this email already exists
        if (userRepository.findByEmail(hrDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (companyRepository.findByName(hrDto.getCompany()).isEmpty()) {
            throw new RuntimeException("Email already in use");
        }

        // 2. Find the company
        Company company = companyRepository.findByName(hrDto.getCompany())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + hrDto.getCompany()));

        // 3. Find or create HR role
        Role hrRole = roleRepository.findByName("HR")
                .orElseThrow(() -> new RuntimeException("HR role not found in database"));

        // 4. Create and save User
        User user = new User();
        user.setUsername(hrDto.getUsername());
        user.setEmail(hrDto.getEmail());
        user.setFname(hrDto.getFname());
        user.setPassword(passwordEncoder.encode(hrDto.getPassword()));
        user.setRole(hrRole);
        User savedUser = userRepository.save(user);

        // 5. Create and save HR
        Hr hr = new Hr();
        hr.setUser(savedUser);
        hr.setCompany(company);

        hrRepository.save(hr);
        return true;
    }
    public boolean hrExists(Long id) {
        System.out.println(hrRepository.existsById(id));
        return hrRepository.existsById(id);
    }
    @Transactional
    public boolean DeleteHr(Long id) {
        Optional<Hr> optionalHr = hrRepository.findById(id);
        if (optionalHr.isEmpty()) {
            return false;
        }

        Hr hr = optionalHr.get();
        try {
            // Delete HR record first to maintain referential integrity
            hrRepository.delete(hr);

            // Then delete the associated user
            userRepository.delete(hr.getUser());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete HR: " + e.getMessage());
        }
    }
}