package com.example.coursetable_system.controller;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.service.CourseInsertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseInsertService courseInsertService;

    public CourseController(CourseInsertService courseInsertService) {
        this.courseInsertService = courseInsertService;
    }

    // 新增单门课程（手动录入）
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        // 检查必要参数
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty() ||
                course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("课程号和课程名称不能为空");
        }
        try {
            Course saved = courseInsertService.saveCourse(course);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 批量新增课程（Excel导入）
    @PostMapping("/import")
    public ResponseEntity<?> importCourses(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        try {
            List<Course> importedCourses = courseInsertService.importCoursesFromExcel(file);
            return new ResponseEntity<>(importedCourses, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件读取失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}