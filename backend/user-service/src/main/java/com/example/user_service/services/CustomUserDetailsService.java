package com.example.user_service.services;

import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.repository.StudentRepository;
import com.example.user_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    private StudentRepository studentRepository;

    public CustomUserDetailsService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername());
        builder.password(appUser.getPassword());
        builder.roles("USER"); // Adjust if you have roles in DB

        return builder.build();
    }*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User appUser = optionalUser.get();

            UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername());
            builder.password(appUser.getPassword());

            // Assuming your User entity has a getRole() method
            builder.roles(appUser.getRole().getName().toUpperCase()); // e.g., ADMIN, HR
            return builder.build();
        }

//        // If not in userRepository, check studentRepository
//        Optional<Student> optionalStudent = studentRepository.findByUserUsername(username);
//
//        if (optionalStudent.isPresent()) {
//            Student student = optionalStudent.get();
//
//            UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(student.getUsername());
//            builder.password(student.getPassword());
//            builder.roles("STUDENT");
//
//            return builder.build();
//        }

        // Not found in either repo
        throw new UsernameNotFoundException("User not found in either user or student repository");
    }

}
