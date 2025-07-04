package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Student;

public interface UserService {
    Student register(Student student) throws Exception;
    String login(String username, String password) throws Exception;
    Student findByUsername(String username);
}