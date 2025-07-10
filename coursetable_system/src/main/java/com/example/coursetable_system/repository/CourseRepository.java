package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // 1. 根据学生 ID 查询课程（通过中间表关联）
    @Query("SELECT cs.course FROM CourseSelection cs WHERE cs.student.studentId = :studentId")
    List<Course> findByStudentId(@Param("studentId") String studentId);

    // 2. 根据学生 ID 和星期查询课程（通过中间表关联，新增方法）
    @Query("SELECT cs.course FROM CourseSelection cs WHERE cs.student.studentId = :studentId " +
            "AND cs.course.dayOfWeek LIKE :dayPattern")
    List<Course> findByStudentIdAndDayOfWeekPattern(
            @Param("studentId") String studentId,
            @Param("dayPattern") String dayPattern
    );
}