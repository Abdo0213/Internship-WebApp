package com.example.user_service.dto;

public class StudentDto {
    private String email;
    private String username;
    private String password;
    private String phone;
    private String college;
    private String faculty;
    private String grade;
    private String cv;
    private String yearOfGraduation;
    private String fName;

    public String getCollege() {
        return college;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getGrade() {
        return grade;
    }

    public String getCv() {
        return cv;
    }

    public String getYearOfGraduation() {
        return yearOfGraduation;
    }
    public String getfName() {
        return fName;
    }

}