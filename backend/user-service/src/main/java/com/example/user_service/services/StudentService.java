package com.example.user_service.services;

import com.example.user_service.model.Student;
import com.example.user_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public Student getUserByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public boolean updateStudent(Long id, Student updatedStudent) {
        Optional<Student> existingStudentOpt = studentRepository.findById(id);

        if (existingStudentOpt.isEmpty()) {
            return false;
        }

        Student existingStudent = existingStudentOpt.get();
        boolean wasUpdated = false;

        // Check and update each field
        if (updatedStudent.getFName() != null && !updatedStudent.getFName().equals(existingStudent.getFName())) {
            existingStudent.setFName(updatedStudent.getFName());
            wasUpdated = true;
        }
        if (updatedStudent.getPhone() != null && !updatedStudent.getPhone().equals(existingStudent.getPhone())) {
            existingStudent.setPhone(updatedStudent.getPhone());
            wasUpdated = true;
        }
        if (updatedStudent.getCollege() != null && !updatedStudent.getCollege().equals(existingStudent.getCollege())) {
            existingStudent.setCollege(updatedStudent.getCollege());
            wasUpdated = true;
        }
        if (updatedStudent.getFaculty() != null && !updatedStudent.getFaculty().equals(existingStudent.getFaculty())) {
            existingStudent.setFaculty(updatedStudent.getFaculty());
            wasUpdated = true;
        }
        if (updatedStudent.getGrade() != null && !updatedStudent.getGrade().equals(existingStudent.getGrade())) {
            existingStudent.setGrade(updatedStudent.getGrade());
            wasUpdated = true;
        }
        if (updatedStudent.getYearOfGraduation() != null && !updatedStudent.getYearOfGraduation().equals(existingStudent.getYearOfGraduation())) {
            existingStudent.setYearOfGraduation(updatedStudent.getYearOfGraduation());
            wasUpdated = true;
        }
        if (updatedStudent.getCv() != null && !updatedStudent.getCv().equals(existingStudent.getCv())) {
            existingStudent.setCv(updatedStudent.getCv());
            wasUpdated = true;
        }

        // Handle password separately
        if (updatedStudent.getPassword() != null && !updatedStudent.getPassword().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(updatedStudent.getPassword()));
            wasUpdated = true;
        }

        if (wasUpdated) {
            studentRepository.save(existingStudent);
        }

        return wasUpdated;
    }
    public Boolean DeleteStudent(Long id){
        if(studentRepository.existsById(id)){
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
