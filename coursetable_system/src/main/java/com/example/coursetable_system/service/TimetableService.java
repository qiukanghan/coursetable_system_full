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

    // 获取周视图课表
    public Map<Integer, List<Course>> getWeeklyTimetable(String studentId) {
        List<Course> courses = courseRepository.findByStudentId(studentId);

        Map<Integer, List<Course>> weeklyTimetable = new TreeMap<>();
        for (int day = 1; day <= 7; day++) {
            weeklyTimetable.put(day, new ArrayList<>());
        }

        // 从"1-10-00"格式中提取星期部分（第一位数字）
        courses.forEach(course -> {
            String dayOfWeekStr = course.getDayOfWeek();
            if (dayOfWeekStr != null && !dayOfWeekStr.isEmpty()) {
                try {
                    int dayOfWeek = Integer.parseInt(dayOfWeekStr.split("-")[0]); // 提取星期部分
                    if (dayOfWeek >= 1 && dayOfWeek <= 7) {
                        weeklyTimetable.get(dayOfWeek).add(course);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // 格式错误，忽略该课程或记录日志
                }
            }
        });

        // 按时间排序（从"1-10-00"中提取时间部分）
        weeklyTimetable.values().forEach(list ->
                list.sort(Comparator.comparing(course -> {
                    String[] parts = course.getDayOfWeek().split("-");
                    return parts.length >= 3 ? parts[1] + parts[2] : ""; // 按"时+分"排序
                }))
        );

        return weeklyTimetable;
    }

    // 获取日视图课表
    public List<Course> getDailyTimetable(String studentId, Integer dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("星期参数无效");
        }

        // 构造匹配格式（如"1-%-%""表示星期一的所有课程）
        String dayPattern = dayOfWeek + "-%";
        List<Course> courses = courseRepository.findByStudentIdAndDayOfWeekPattern(studentId, dayPattern);

        // 按时间排序（从"1-10-00"中提取时间部分）
        courses.sort(Comparator.comparing(course -> {
            String[] parts = course.getDayOfWeek().split("-");
            return parts.length >= 3 ? parts[1] + parts[2] : ""; // 按"时+分"排序
        }));

        return courses;
    }
}