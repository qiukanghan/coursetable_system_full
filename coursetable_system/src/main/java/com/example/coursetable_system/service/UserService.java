package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Student;
import com.example.coursetable_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Student register(Student student) throws Exception {
        if (studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
            throw new Exception("学号已存在：" + student.getStudentId());
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    public String login(String studentId, String password) throws Exception {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new Exception("学号不存在：" + studentId));
        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new Exception("密码错误");
        }
        return UUID.randomUUID().toString();
    }

    public Student findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }
}