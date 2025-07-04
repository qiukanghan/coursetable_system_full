package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    // 根据学号查询学生（校验学生是否存在）
    Optional<Student> findByStudentId(String studentId);
}
