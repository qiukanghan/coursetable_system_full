package com.example.coursetable_system.controller;

import com.example.coursetable_system.entity.Student;
import com.example.coursetable_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student student) {
        try {
            Student registeredStudent = userService.register(student);
            return new ResponseEntity<>(registeredStudent.getStudentId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student student) {
        try {
            String token = userService.login(student.getStudentId(), student.getPassword());
            // 仅返回studentId，与register方法保持一致
            return ResponseEntity.ok(student.getStudentId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}