package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_category")
public class CourseCategory {
    @Id
    @Column(name = "category_id")
    private Integer categoryId; // 类别ID（主键，1=必修，2=选修）

    @Column(name = "category_name", nullable = false, unique = true, length = 20)
    private String categoryName; // 类别名（如“必修”“选修”）
}