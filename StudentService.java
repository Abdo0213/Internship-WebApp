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
    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
            if (students.isEmpty())
            throw new RuntimeException("No students found");
        return students;
    }
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public Boolean UpdateStudent(Long id, Student UpdateStudent){
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        if (UpdateStudent.getUsername() != null && !student.getUsername().equals(UpdateStudent.getUsername())) 
            student.setUsername(UpdateStudent.getUsername());
        if (UpdateStudent.getPassword() != null && !student.getPassword().equals(UpdateStudent.getPassword()))
            student.setPassword(UpdateStudent.getPassword());
        if (UpdateStudent.getEmail() != null && !student.getEmail().equals(UpdateStudent.getEmail())) 
            student.setEmail(UpdateStudent.getEmail());
        if (UpdateStudent.getPhone() != null && !student.getPhone().equals(UpdateStudent.getPhone())) 
            student.setPhone(UpdateStudent.getPhone());
        if (UpdateStudent.getCollege() != null && !student.getCollege().equals(UpdateStudent.getCollege())) 
            student.setCollege(UpdateStudent.getCollege());
        if (UpdateStudent.getFaculty() != null && !student.getFaculty().equals(UpdateStudent.getFaculty())) 
            student.setFaculty(UpdateStudent.getFaculty());
        if (UpdateStudent.getGrade() != null && !student.getGrade().equals(UpdateStudent.getGrade())) 
            student.setGrade(UpdateStudent.getGrade());
        if (UpdateStudent.getCv() != null && !student.getCv().equals(UpdateStudent.getCv())) 
            student.setCv(UpdateStudent.getCv());
        if (UpdateStudent.getYearOfGraduation() != null && !student.getYearOfGraduation().equals(UpdateStudent.getYearOfGraduation())) 
            student.setYearOfGraduation(UpdateStudent.getYearOfGraduation());
        if (UpdateStudent.getFName() != null && !student.getFName().equals(UpdateStudent.getFName())) 
            student.setFName(UpdateStudent.getFName());
            
    studentRepository.save(student);
    return true;
    }
    public Boolean DeleteStudent(Long id){
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        } else {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }
}
