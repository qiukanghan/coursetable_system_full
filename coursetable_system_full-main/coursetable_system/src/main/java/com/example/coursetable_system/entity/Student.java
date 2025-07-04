package com.example.coursetable_system.entity;

/*
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
*/

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long student_id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createTime;

    // Getters and Setters
    public long getId() { return student_id; }
    public void setId(long student_id) { this.student_id = student_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
