package com.example.user_service.controller;

import com.example.user_service.annotation.JwtValidation;
import com.example.user_service.dto.StudentDto;
import com.example.user_service.model.Student;
import com.example.user_service.services.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<String> getAllStudents() throws JsonProcessingException {
        try {
            List<Student> students = studentService.getAllStudents();

            if (students.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No users found");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String usersJson = objectMapper.writeValueAsString(students);
            return ResponseEntity.ok(usersJson);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing user data");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<String> getOneStudent(@PathVariable Long id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudentById(id);  // Returns null if not found

            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }

            // Convert user to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String userJson = mapper.writeValueAsString(student);
            return ResponseEntity.ok(userJson);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error retrieving student: " + e.getMessage());
        }
    }
    @GetMapping("/exists/{id}")
    @JwtValidation(requiredRoles = {"student"}) // Adjust roles as needed
    public ResponseEntity<Void> checkHrExists(@PathVariable Long id) {
        return studentService.studentExists(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody StudentDto request) {
        try {
            boolean isUpdated = studentService.updateStudent(id, request);
            if (isUpdated) {
                return ResponseEntity.ok("Student updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("No changes detected");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) throws JsonProcessingException {
        try {
            boolean isDeleted = studentService.DeleteStudent(id);

            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Student deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with ID: " + id);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

}
/*
get all users
get users (id) --> get one user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */
