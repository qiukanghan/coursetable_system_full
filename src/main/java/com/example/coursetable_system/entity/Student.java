package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "student_id", length = 20)
    private String studentId; // 学号（主键）

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 姓名

    @Column(name = "password", nullable = false, length = 100)
    private String password; // 密码（建议加密存储）

    @Column(name = "email", length = 100)
    private String email; // 邮箱
}