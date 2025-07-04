package com.example.coursetable_system.controller;

import com.example.coursetable_system.dto.CourseDTO;
import com.example.coursetable_system.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    @Autowired
    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // 获取周视图课表
    @GetMapping("/weekly")
    public ResponseEntity<Map<Integer, List<CourseDTO>>> getWeeklyTimetable(
            @AuthenticationPrincipal(expression = "id") Long userId) {

        Map<Integer, List<CourseDTO>> weeklyData =
                timetableService.getWeeklyTimetable(userId);

        return ResponseEntity.ok(weeklyData);
    }

    // 获取日视图课表
    @GetMapping("/daily")
    public ResponseEntity<List<CourseDTO>> getDailyTimetable(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam("day") Integer dayOfWeek) {

        // 验证星期参数有效性 (1-7)
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            return ResponseEntity.badRequest().body(null);
        }

        List<CourseDTO> dailyData =
                timetableService.getDailyTimetable(userId, dayOfWeek);

        return ResponseEntity.ok(dailyData);
    }
}