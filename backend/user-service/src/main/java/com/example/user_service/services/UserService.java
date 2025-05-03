package com.example.user_service.services;


import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> getUserAllUsers(Long id) {
        // get all the users and return it in list ----> 6
        return null;
    }

    // not assigned
    public Boolean AddUser(String userName, String password, String email, String company){
        // need to add the new hr user with the bcrypt password ----> 5
        return false;
    }
    public Boolean UpdateUser(String userName, String password, String email, String company){
        // need to add the new hr user with the bcrypt password ----> 4
        return false;
    }
    public Boolean DeleteUser(Long id){
        // need to delete hr user with the id
        return false;
    }

}

/*
get all users
get users (id) --> get one user
post users --> create user
put users prefetch (id) --> update info for one
delete users (id) --> delete user

 */