package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "course_selection")
// 复合主键类（需实现Serializable）
@IdClass(CourseSelectionId.class)
public class CourseSelection {
    // 关联学生（复合主键之一）
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    // 关联课程（复合主键之一）
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;
}

