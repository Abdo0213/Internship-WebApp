package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.services.StudentService;
import com.example.user_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;


    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public ResponseEntity<String> getAllStudents() throws JsonProcessingException {
        // I want her a function from the service to get all students in list --> 1
        return null;
    }

    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<String> getOneStudent(@PathVariable Long id) throws JsonProcessingException {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok("very nice");
    }

    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<String> updateStudent(@PathVariable Long id) throws JsonProcessingException {
        // function to get the user and take its data from the body then call the function in service to update it ---> 2
        return null;
    }
    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"admin"})
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) throws JsonProcessingException {
        Boolean isDeleted = studentService.DeleteStudent(id);
        return null;
    }



}
/*
get all users
get users (id) --> get one user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */
