package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final CourseRepository courseRepository;

    @Autowired
    public TimetableService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // 获取周视图课表，参数为 String 类型的 studentId
    public Map<Integer, List<Course>> getWeeklyTimetable(String studentId) {
        List<Course> courses = courseRepository.findByStudentId(studentId);

        Map<Integer, List<Course>> weeklyTimetable = new TreeMap<>();
        // 初始化周一到周日的课程列表
        for (int day = 1; day <= 7; day++) {
            weeklyTimetable.put(day, new ArrayList<>());
        }

        // 按星期分组课程
        courses.forEach(course -> {
            Integer dayOfWeek = course.getDayOfWeek();
            if (dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 7) {
                weeklyTimetable.get(dayOfWeek).add(course);
            }
        });

        // 按开始时间排序每天的课程
        weeklyTimetable.values().forEach(list ->
                list.sort(Comparator.comparing(Course::getStartTime))
        );

        return weeklyTimetable;
    }

    // 获取日视图课表，参数为 String 类型的 studentId
    public List<Course> getDailyTimetable(String studentId, Integer dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("星期参数无效");
        }

        List<Course> courses = courseRepository.findByStudentIdAndDayOfWeek(studentId, dayOfWeek);
        // 按开始时间排序
        courses.sort(Comparator.comparing(Course::getStartTime));
        return courses;
    }
}