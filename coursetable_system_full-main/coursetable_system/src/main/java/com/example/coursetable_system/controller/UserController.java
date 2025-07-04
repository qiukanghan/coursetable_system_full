package com.example.coursetable_system.controller;

import com.example.coursetable_system.entity.Student;
import com.example.coursetable_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            return ResponseEntity.ok().body(new Result<>(200, "success", registeredStudent.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Result<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student student) {
        try {
            String token = userService.login(student.getUsername(), student.getPassword());
            Student loggedInStudent = userService.findByUsername(student.getUsername());
            return ResponseEntity.ok().body(new Result<>(200, "success", new LoginResponse(token, loggedInStudent.getId(), loggedInStudent.getUsername())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Result<>(400, e.getMessage(), null));
        }
    }
}

// 辅助类
class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}

class LoginResponse {
    private String token;
    private long id;
    private String username;

    public LoginResponse(String token, long id, String username) {
        this.token = token;
        this.id = id;
        this.username = username;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public long getId() { return id; }
    public String getUsername() { return username; }
}