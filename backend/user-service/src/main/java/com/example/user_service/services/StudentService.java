package com.example.user_service.services;

import com.example.user_service.dto.StudentDto;
import com.example.user_service.model.Student;
import com.example.user_service.model.User;
import com.example.user_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, UserService userService) {
        this.studentRepository = studentRepository;
        this.userService = userService;
    }
    public Student getUserByUsername(String username) {
        return studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public void addStudent(StudentDto dto) {
        userService.AddUser(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getfName(),"STUDENT");

        User user =  userService.getUserByUsername(dto.getUsername());
        Student student = new Student();
        student.setUser(user);
        student.setFaculty(dto.getFaculty());
        student.setCollege(dto.getCollege());
        student.setGrade(dto.getGrade());
        student.setYearOfGraduation(dto.getYearOfGraduation());
        student.setPhone(dto.getPhone());
        student.setCv(dto.getCv());
        studentRepository.save(student);
    }
    public boolean updateStudent(Long id, StudentDto updatedStudent) {
        Optional<Student> existingStudentOpt = studentRepository.findById(id);

        if (existingStudentOpt.isEmpty()) {
            return false;
        }

        Student existingStudent = existingStudentOpt.get();
        boolean wasUpdated = false;

        // Check and update each field

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
        User user = existingStudent.getUser();
        System.out.println(updatedStudent.getfName() + "-------------------------------");
        // Handle password separately
        if (updatedStudent.getPassword() != null && !updatedStudent.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedStudent.getPassword()));
            wasUpdated = true;
        }
        if (updatedStudent.getEmail() != null && !updatedStudent.getEmail().isEmpty()) {
            user.setEmail(updatedStudent.getEmail());
            wasUpdated = true;
        }
        if (updatedStudent.getUsername() != null && !updatedStudent.getUsername().isEmpty()) {
            user.setUsername(updatedStudent.getUsername());
            wasUpdated = true;
        }
        if (updatedStudent.getfName() != null && !updatedStudent.getfName().equals(user.getFname())) {
            user.setFname(updatedStudent.getfName());
            wasUpdated = true;
        }
        if (wasUpdated) {
            userService.UpdateUser(user.getId(), user.getUsername(), updatedStudent.getPassword(), user.getEmail(), user.getFname(), user.getRole().getName());
            studentRepository.save(existingStudent);
        }

        return wasUpdated;
    }
    public boolean studentExists(Long id) {
        return studentRepository.existsById(id);
    }
    public Boolean DeleteStudent(Long id){
        if(studentRepository.existsById(id)){
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
