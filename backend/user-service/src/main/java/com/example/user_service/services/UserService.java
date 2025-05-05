package com.example.user_service.services;


import com.example.user_service.model.*;
import com.example.user_service.repository.CompanyRepository;
import com.example.user_service.repository.HrRepository;
import com.example.user_service.repository.RoleRepository;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, HrRepository hrRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public Boolean AddUser(String username, String password, String email, String FName, String roleName){
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> userOpt2 = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            throw new RuntimeException("User Name Is Already Exists");
        }
        if(userOpt2.isPresent()){
            throw new RuntimeException("Email Is Already Exists");
        }
        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setFname(FName);
        newUser.setRole(role);

        userRepository.save(newUser);
        return true;
    }
    public Boolean UpdateUser(Long id, String userName, String password, String email, String FName, String roleName) {
        // 1. Find the existing user by ID (or throw an exception if not found)
        boolean isChanged = false;
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        User existingUser = existingUserOpt.get();

        // 2. Check if the new username or email already exists (excluding the current user)
        if (userName != null && !userName.equals(existingUser.getUsername())) {
            Optional<User> userWithSameUsername = userRepository.findByUsername(userName);
            if (userWithSameUsername.isPresent()) {
                throw new RuntimeException("Username already taken");
            }
        }

        if (email != null && !email.equals(existingUser.getEmail())) {
            Optional<User> userWithSameEmail = userRepository.findByEmail(email);
            if (userWithSameEmail.isPresent()) {
                throw new RuntimeException("Email already in use");
            }
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        // 3. Update fields if they are provided (non-null)
        if (userName != null) {
            existingUser.setUsername(userName);
            isChanged = true;
        }
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password)); // Hash new password
            isChanged = true;
        }
        if (email != null) {
            existingUser.setEmail(email);
            isChanged = true;
        }
        if (FName != null) {
            existingUser.setFname(FName);
            isChanged = true;
        }
        if (role != null) {
            existingUser.setRole(role);
            isChanged = true;
        }

        // 4. Save the updated user
        if (isChanged) {
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }
    public Boolean DeleteUser(Long id){
        if(!userRepository.existsById(id)){
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

}

/*
get all users
get users (id) --> get one user
post users --> create user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */