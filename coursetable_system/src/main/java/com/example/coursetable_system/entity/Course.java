package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @Column(name = "course_id", length = 20)
    private String courseId; // 课程号（主键）

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName; // 课程名

    @Column(name = "teacher", length = 50)
    private String teacher; // 授课老师

    @Column(name = "location", length = 100)
    private String location; // 上课地点

    @Column(name = "day_of_week", length = 20) // 类型改为varchar(20)
    private String dayOfWeek; // 字段类型从Integer改为String（存储格式如"1-10-00"）

    // 删除以下两个字段（对应数据库表删除的start_time和end_time）
    // @Column(name = "start_time")
    // private LocalTime startTime;

    // @Column(name = "end_time")
    // private LocalTime endTime;

    @Column(name = "category_id")
    private Long categoryId; // 课程类别 ID
}