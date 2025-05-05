package com.example.user_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String cv;
    private String fName;

    private String roleName;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }


    public String getfName() {
        return fName;
    }

    public String getCv() {
        return cv;
    }

    public String getRoleName() {
        return roleName;
    }
}