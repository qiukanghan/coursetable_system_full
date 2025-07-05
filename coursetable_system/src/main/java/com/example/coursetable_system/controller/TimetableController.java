package com.example.coursetable_system.controller;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.service.TimetableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    // 构造函数注入 Service，和 CourseController 风格一致
    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // 获取周视图课表，通过 @RequestParam 接收 studentId
    @GetMapping("/weekly")
    public ResponseEntity<Map<Integer, List<Course>>> getWeeklyTimetable(
            @RequestParam String studentId) {  // 显式传递学号，与 CourseController 对齐
        try {
            Map<Integer, List<Course>> weeklyData = timetableService.getWeeklyTimetable(studentId);
            return ResponseEntity.ok(weeklyData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 获取日视图课表，通过 @RequestParam 接收 studentId 和 dayOfWeek
    @GetMapping("/daily")
    public ResponseEntity<List<Course>> getDailyTimetable(
            @RequestParam String studentId,  // 显式传递学号
            @RequestParam("day") Integer dayOfWeek) {

        // 验证星期参数有效性 (1-7)
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            List<Course> dailyData = timetableService.getDailyTimetable(studentId, dayOfWeek);
            return ResponseEntity.ok(dailyData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}