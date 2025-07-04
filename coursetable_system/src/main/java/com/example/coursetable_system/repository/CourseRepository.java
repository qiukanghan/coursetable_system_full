package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // 根据用户ID查找课程
    List<Course> findByUserId(Long userId);

    // 根据用户ID和星期查找课程
    List<Course> findByUserIdAndDayOfWeek(Long userId, Integer dayOfWeek);
}