package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.repository.CourseRepository;
import com.example.coursetable_system.dto.CourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final CourseRepository courseRepository;

    @Autowired
    public TimetableService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // 获取周视图课表
    public Map<Integer, List<CourseDTO>> getWeeklyTimetable(Long userId) {
        // 获取用户所有课程
        List<Course> courses = courseRepository.findByUserId(userId);

        // 按星期分组 (1-7 表示周一到周日)
        Map<Integer, List<CourseDTO>> weeklyTimetable = new TreeMap<>();

        // 初始化空列表
        for (int day = 1; day <= 7; day++) {
            weeklyTimetable.put(day, new ArrayList<>());
        }

        // 转换DTO并分组
        courses.stream()
                .map(this::convertToDTO)
                .forEach(dto -> weeklyTimetable.get(dto.getDayOfWeek()).add(dto));

        // 按时间排序每天的课程
        weeklyTimetable.values().forEach(list ->
                list.sort(Comparator.comparing(CourseDTO::getStartTime)));

        return weeklyTimetable;
    }

    // 获取日视图课表
    public List<CourseDTO> getDailyTimetable(Long userId, Integer dayOfWeek) {
        return courseRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek)
                .stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(CourseDTO::getStartTime))
                .collect(Collectors.toList());
    }

    // 实体转DTO（含颜色标签）
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setTeacher(course.getTeacher());
        dto.setLocation(course.getLocation());
        dto.setDayOfWeek(course.getDayOfWeek());
        dto.setStartTime(course.getStartTime());
        dto.setEndTime(course.getEndTime());
        dto.setCategoryId(course.getCategoryId());

        // 根据类别ID设置颜色标签
        dto.setColorTag(getColorByCategory(course.getCategoryId()));

        return dto;
    }

    // 根据类别ID获取颜色（示例实现，实际应从数据库获取）
    private String getColorByCategory(Long categoryId) {
        if (categoryId == null) return "#CCCCCC"; // 默认灰色

        // 这里应该是从数据库获取颜色配置，简化示例
        Map<Long, String> colorMap = new HashMap<>();
        colorMap.put(1L, "#FF6B6B"); // 必修课 - 红色
        colorMap.put(2L, "#4ECDC4"); // 选修课 - 绿色
        colorMap.put(3L, "#FFD166"); // 实验课 - 黄色
        colorMap.put(4L, "#118AB2"); // 体育课 - 蓝色

        return colorMap.getOrDefault(categoryId, "#CCCCCC");
    }
}