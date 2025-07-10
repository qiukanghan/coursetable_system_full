package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.entity.CourseSelection;
import com.example.coursetable_system.entity.Student;
import com.example.coursetable_system.repository.CourseRepository;
import com.example.coursetable_system.repository.CourseSelectionRepository;
import com.example.coursetable_system.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseSelectionRepository selectionRepository;
    private final StudentRepository studentRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // 构造函数注入依赖
    public CourseService(CourseRepository courseRepository,
                         CourseSelectionRepository selectionRepository,
                         StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.selectionRepository = selectionRepository;
        this.studentRepository = studentRepository;
    }

    // ========== 1. 手动录入课程（需传递 studentId） ==========
    @Transactional
    public Course saveCourse(Course course, String studentId) {
        // 1. 校验学生是否存在
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在：" + studentId));

        // 2. 校验课程号是否已存在
        if (courseRepository.existsById(course.getCourseId())) {
            throw new IllegalArgumentException("课程号已存在：" + course.getCourseId());
        }

        // 3. 保存课程
        Course savedCourse = courseRepository.save(course);

        // 4. 关联学生-课程（写入中间表）
        CourseSelection selection = new CourseSelection();
        selection.setStudent(student);
        selection.setCourse(savedCourse);
        selectionRepository.save(selection);

        return savedCourse;
    }

    // ========== 2. Excel 批量导入课程（需传递 studentId） ==========
    @Transactional
    public List<Course> importCoursesFromExcel(MultipartFile file, String studentId) throws IOException {
        // 1. 校验学生是否存在
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在：" + studentId));

        // 2. 解析 Excel 文件
        List<Course> courses = parseExcel(file);

        // 3. 校验 Excel 内课程号是否重复
        List<String> courseIds = new ArrayList<>();
        for (Course course : courses) {
            if (courseIds.contains(course.getCourseId())) {
                throw new IllegalArgumentException("Excel 中存在重复课程号：" + course.getCourseId());
            }
            courseIds.add(course.getCourseId());
        }

        // 4. 校验课程号是否已存在于数据库
        for (String courseId : courseIds) {
            if (courseRepository.existsById(courseId)) {
                throw new IllegalArgumentException("课程号已存在于系统：" + courseId);
            }
        }

        // 5. 保存课程
        List<Course> savedCourses = courseRepository.saveAll(courses);

        // 6. 批量关联学生-课程（写入中间表）
        List<CourseSelection> selections = new ArrayList<>();
        for (Course course : savedCourses) {
            CourseSelection selection = new CourseSelection();
            selection.setStudent(student);
            selection.setCourse(course);
            selections.add(selection);
        }
        selectionRepository.saveAll(selections);

        return savedCourses;
    }

    // ========== 3. 查询我的课程列表（需传递 studentId） ==========
    public List<Course> listMyCourses(String studentId) {
        // 校验学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("学生不存在：" + studentId);
        }
        // 查询该学生的所有课程
        return courseRepository.findByStudentId(studentId);
    }

    // ========== 4. 更新课程信息（需传递 studentId，仅能改自己的课程） ==========
    @Transactional
    public Course updateCourse(Course updatedCourse, String studentId) {
        // 1. 校验学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("学生不存在：" + studentId);
        }

        // 2. 校验课程是否存在
        Course existingCourse = courseRepository.findById(updatedCourse.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("课程不存在：" + updatedCourse.getCourseId()));

        // 3. 校验是否是自己的课程（通过中间表）
        if (!selectionRepository.existsByStudent_StudentIdAndCourse_CourseId(studentId, existingCourse.getCourseId())) {
            throw new IllegalArgumentException("无权修改他人课程：" + existingCourse.getCourseId());
        }

        // 4. 更新课程字段（删除startTime和endTime的更新，dayOfWeek类型改为String）
        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setTeacher(updatedCourse.getTeacher());
        existingCourse.setLocation(updatedCourse.getLocation());
        existingCourse.setDayOfWeek(updatedCourse.getDayOfWeek()); // 现在接收String类型
        existingCourse.setCategoryId(updatedCourse.getCategoryId());

        return courseRepository.save(existingCourse);
    }

    // ========== 5. 删除课程（需传递 studentId，仅能删自己的课程） ==========
    @Transactional
    public void deleteCourse(String courseId, String studentId) {
        // 1. 校验学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("学生不存在：" + studentId);
        }

        // 2. 校验课程是否存在
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("课程不存在：" + courseId);
        }

        // 3. 校验是否是自己的课程（通过中间表）
        if (!selectionRepository.existsByStudent_StudentIdAndCourse_CourseId(studentId, courseId)) {
            throw new IllegalArgumentException("无权删除他人课程：" + courseId);
        }

        // 4. 删除课程及中间表关联
        courseRepository.deleteById(courseId);
        selectionRepository.deleteByCourse_CourseId(courseId);
    }

    // ========== 辅助方法：解析 Excel 文件 ==========
    private List<Course> parseExcel(MultipartFile file) throws IOException {
        List<Course> courses = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表
            boolean isHeaderRow = true; // 跳过标题行

            for (Row row : sheet) {
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue;
                }

                // 校验行数据是否完整（至少包含课程号和课程名）
                if (row.getCell(0) == null || row.getCell(1) == null) {
                    throw new IllegalArgumentException("Excel 行数据不完整，行号：" + (row.getRowNum() + 1));
                }

                Course course = new Course();

                // 按列解析（假设 Excel 列顺序：课程号、课程名、教师、地点、星期、开始时间、结束时间、类别 ID）
                course.setCourseId(getStringValue(row.getCell(0)));
                course.setCourseName(getStringValue(row.getCell(1)));
                course.setTeacher(getStringValue(row.getCell(2)));
                course.setLocation(getStringValue(row.getCell(3)));
                course.setDayOfWeek(getStringValue(row.getCell(4))); // 不再强转为Integer
                course.setCategoryId((long) row.getCell(5).getNumericCellValue()); // 类别 ID

                courses.add(course);
            }
        }
        return courses;
    }

    // 辅助方法：从单元格获取字符串值
    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue()); // 数字转字符串（如学号）
        }
        return "";
    }

    // 辅助方法：从单元格获取时间字符串（适配 Excel 时间格式）
    private String getTimeString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim(); // 字符串格式时间（如 "08:30"）
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Excel 时间格式转 LocalTime（数值代表天数，需转换为纳秒）
            return LocalTime.ofNanoOfDay(
                    (long) (cell.getNumericCellValue() * 24 * 60 * 60 * 1_000_000_000)
            ).format(timeFormatter);
        }
        return "";
    }
}