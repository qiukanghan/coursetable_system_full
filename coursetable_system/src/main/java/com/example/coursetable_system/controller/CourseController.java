package com.example.coursetable_system.controller;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    // 构造函数注入 Service
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ========== 1. 手动录入课程（需传递 studentId） ==========
    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody Course course,
            @RequestParam String studentId // 前端传递学号
    ) {
        try {
            Course savedCourse = courseService.saveCourse(course, studentId);
            return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 返回错误信息
        }
    }

    // ========== 2. Excel 批量导入课程（需传递 studentId） ==========
    @PostMapping("/import")
    public ResponseEntity<?> importCourses(
            @RequestParam("file") MultipartFile file,
            @RequestParam String studentId // 前端传递学号
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        try {
            List<Course> importedCourses = courseService.importCoursesFromExcel(file, studentId);
            return new ResponseEntity<>(importedCourses, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文件读取失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 返回错误信息（如课程号重复）
        }
    }

    // ========== 3. 查询我的课程列表（需传递 studentId） ==========
    @GetMapping("/my")
    public ResponseEntity<?> listMyCourses(
            @RequestParam String studentId // 前端传递学号
    ) {
        try {
            List<Course> myCourses = courseService.listMyCourses(studentId);
            return ResponseEntity.ok(myCourses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========== 4. 更新课程信息（需传递 studentId，仅能改自己的课程） ==========
    @PutMapping
    public ResponseEntity<?> updateCourse(
            @RequestBody Course course,
            @RequestParam String studentId // 前端传递学号
    ) {
        try {
            Course updatedCourse = courseService.updateCourse(course, studentId);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 如“无权修改”
        }
    }

    // ========== 5. 删除课程（需传递 studentId，仅能删自己的课程） ==========
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable String courseId,
            @RequestParam String studentId // 前端传递学号
    ) {
        try {
            courseService.deleteCourse(courseId, studentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 成功删除无返回体
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 如“无权删除”
        }
    }
}