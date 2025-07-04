package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Student;
import com.example.coursetable_system.repository.StudentRepository;
import com.example.coursetable_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Student register(Student student) throws Exception {
        if (studentRepository.findByUsername(student.getUsername()) != null) {
            throw new Exception("Username already exists");
        }
        if (studentRepository.findByEmail(student.getEmail()) != null) {
            throw new Exception("Email already exists");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setCreateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public String login(String username, String password) throws Exception {
        Student student = studentRepository.findByUsername(username);
        if (student == null || !passwordEncoder.matches(password, student.getPassword())) {
            throw new Exception("Invalid username or password");
        }
        // 简单生成 Token（实际应使用 JWT）
        return UUID.randomUUID().toString();
    }

    @Override
    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }
}