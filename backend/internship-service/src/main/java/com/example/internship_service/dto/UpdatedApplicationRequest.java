package com.example.internship_service.dto;

import com.example.internship_service.model.Application;

public class UpdatedApplicationRequest {
    public Application.Status status;
    public Long internshipId;

    public Application.Status getStatus() {
        return status;
    }

    public void setStatus(Application.Status status) {
        this.status = status;
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }
}