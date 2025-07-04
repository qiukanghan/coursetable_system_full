package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
    // 根据学生 ID 查询课程（通过中间表 course_selection 关联）
    @Query("SELECT cs.course FROM CourseSelection cs WHERE cs.student.studentId = :studentId")
    List<Course> findByStudentId(String studentId);
}