package com.example.coursetable_system.entity;

import java.io.Serializable;

// 复合主键类（单独定义）
public class CourseSelectionId implements Serializable {
    private String student; // 对应CourseSelection中的student.studentId
    private String course; // 对应CourseSelection中的course.courseId

    // 必须重写equals和hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseSelectionId that = (CourseSelectionId) o;
        return student.equals(that.student) && course.equals(that.course);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(student, course);
    }
}
