package com.example.coursetable_system.DTO;


import lombok.Data;
import java.time.LocalTime;

@Data
public class CourseDTO {
    private String courseId;       // 课程号
    private String courseName;     // 课程名
    private String teacher;        // 授课老师
    private String location;       // 上课地点
    private Integer dayOfWeek;     // 星期几 (1-7)
    private LocalTime startTime;   // 开始时间
    private LocalTime endTime;     // 结束时间
    private Long categoryId;       // 课程类别ID
    private String colorTag;       // 颜色标签
}