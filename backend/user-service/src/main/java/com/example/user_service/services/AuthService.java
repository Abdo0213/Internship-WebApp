package com.example.user_service.services;

import com.example.user_service.model.Student;
import com.example.user_service.model.User;
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

    public void registerUser(String username, String password, String email, String fName, String cv) {
        Optional<Student> userOpt = studentRepository.findByUsername(username);
        Optional<Student> userOpt2 = studentRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            throw new RuntimeException("Username exists");
        } else if (userOpt2.isPresent()){
            throw new RuntimeException("Email exists");
        }
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(password));
        student.setEmail(email);
        student.setFName(fName);
        student.setCv(cv);

        studentRepository.save(student);
    }
}