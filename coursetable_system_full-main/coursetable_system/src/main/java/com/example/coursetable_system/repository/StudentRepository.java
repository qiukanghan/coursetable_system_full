package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUsername(String username);
    Student findByEmail(String email);
}