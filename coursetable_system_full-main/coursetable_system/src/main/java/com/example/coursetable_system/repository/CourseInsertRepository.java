package com.example.coursetable_system.repository;

import com.example.coursetable_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseInsertRepository extends JpaRepository<Course, String> {
}