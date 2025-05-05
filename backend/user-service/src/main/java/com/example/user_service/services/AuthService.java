package com.example.user_service.services;

import com.example.user_service.model.Company;
import com.example.user_service.model.Role;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.repository.CompanyRepository;
import com.example.user_service.repository.RoleRepository;
import com.example.user_service.repository.StudentRepository;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /*public String authenticate(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return auth.getName(); // Returns username
    }*/
    public Map<String, String> authenticate(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");

        Map<String, String> result = new HashMap<>();
        result.put("username", userDetails.getUsername());
        result.put("role", role);

        return result;
    }

    public void registerUser(String username, String password, String email, String fName, String roleName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> userOpt2 = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            throw new RuntimeException("Username exists");
        } else if (userOpt2.isPresent()){
            throw new RuntimeException("Email exists");
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFname(fName);
        user.setRole(role);
        userRepository.save(user);

        Optional<User> newUser = userRepository.findByEmail(user.getEmail());
        if(newUser.isEmpty()){
            throw new RuntimeException("User hasnt saved");
        }
        if(roleName.equals("STUDENT"))
        {
            Student student = new Student();
            student.setUser(newUser.get());
            studentRepository.save(student);
        }
        else if(roleName.equals("Company"))
        {
            Company company = new Company();
            company.setUser(newUser.get());
            companyRepository.save(company);
        }
        else{
            throw new RuntimeException("Role not found");
        }

    }
}