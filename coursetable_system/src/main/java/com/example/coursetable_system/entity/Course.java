package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

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

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 星期（1-7，1=周一）

    @Column(name = "start_time")
    private LocalTime startTime; // 开始时间

    @Column(name = "end_time")
    private LocalTime endTime; // 结束时间

    @Column(name = "category_id")
    private Long categoryId; // 仅保留课程类别 ID
}