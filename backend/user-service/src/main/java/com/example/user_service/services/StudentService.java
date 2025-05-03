package com.example.user_service.services;

import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.repository.StudentRepository;
import com.example.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getUserByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public List<Student> getAllStudents(String username) {
        // here is the function  ---> 1
        return null;
    }
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public Boolean UpdateStudent(Long id){
        // need to update the student here with id ----> 2
        return false;
    }
    public Boolean DeleteStudent(Long id){
        // need to delete hr user with the id ---> 3
        return false;
    }
}
