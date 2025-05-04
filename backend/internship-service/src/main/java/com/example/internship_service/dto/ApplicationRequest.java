package com.example.internship_service.dto;

import lombok.Data;

@Data
public class ApplicationRequest {
    private Long studentId;
    private Long internshipId;
    // Optional: Add other fields like coverLetter if needed

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }
}