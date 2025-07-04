package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.CourseSelection;
import com.example.coursetable_system.entity.CourseSelectionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseSelectionRepository extends JpaRepository<CourseSelection, CourseSelectionId> {
    // 校验学生是否拥有该课程（用于权限控制）
    boolean existsByStudent_StudentIdAndCourse_CourseId(String studentId, String courseId);

    // 根据课程 ID 删除中间表关联（级联删除）
    void deleteByCourse_CourseId(String courseId);
}