package com.example.coursetable_system.service;

import com.example.coursetable_system.entity.Course;
import com.example.coursetable_system.repository.CourseInsertRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseInsertService {
    private final CourseInsertRepository courseInsertRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public CourseInsertService(CourseInsertRepository courseInsertRepository) {
        this.courseInsertRepository = courseInsertRepository;
    }

    // 新增课程（手动录入）
    public Course saveCourse(Course course) {
        // 校验课程号是否已存在
        if (courseInsertRepository.existsById(course.getCourseId())) {
            throw new IllegalArgumentException("课程号已存在：" + course.getCourseId());
        }
        return courseInsertRepository.save(course);
    }

    // 批量新增（Excel导入）
    public List<Course> saveBatchCourses(List<Course> courses) {
        // 检查课程号是否重复
        List<String> courseIds = new ArrayList<>();
        for (Course course : courses) {
            if (courseIds.contains(course.getCourseId())) {
                throw new IllegalArgumentException("Excel中存在重复课程号：" + course.getCourseId());
            }
            courseIds.add(course.getCourseId());
        }
        return courseInsertRepository.saveAll(courses);
    }

    // 从Excel导入课程（适配新的数据模型）
    public List<Course> importCoursesFromExcel(MultipartFile file) throws IOException {
        List<Course> courses = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeaderRow = true;

            for (Row row : sheet) {
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue; // 跳过标题行
                }

                // 确保课程号（第0列）不为空
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                Course course = new Course();

                // 1. 读取课程号（必填，第0列）
                Cell courseIdCell = row.getCell(0);
                String courseId = getStringValue(courseIdCell);
                if (courseId.isEmpty()) {
                    throw new IllegalArgumentException("课程号不能为空，行号：" + (row.getRowNum() + 1));
                }
                course.setCourseId(courseId);

                // 2. 读取课程名称（必填，第1列）
                Cell courseNameCell = row.getCell(1);
                String courseName = getStringValue(courseNameCell);
                if (courseName.isEmpty()) {
                    throw new IllegalArgumentException("课程名称不能为空，行号：" + (row.getRowNum() + 1));
                }
                course.setCourseName(courseName);

                // 3. 读取教师（可选，第2列）
                Cell teacherCell = row.getCell(2);
                if (teacherCell != null && teacherCell.getCellType() != CellType.BLANK) {
                    course.setTeacher(getStringValue(teacherCell));
                }

                // 4. 读取地点（可选，第3列）
                Cell locationCell = row.getCell(3);
                if (locationCell != null && locationCell.getCellType() != CellType.BLANK) {
                    course.setLocation(getStringValue(locationCell));
                }

                // 5. 读取星期（可选，第4列）
                Cell dayOfWeekCell = row.getCell(4);
                if (dayOfWeekCell != null && dayOfWeekCell.getCellType() != CellType.BLANK) {
                    course.setDayOfWeek((int) dayOfWeekCell.getNumericCellValue());
                }

                // 6. 读取开始时间（可选，第5列）
                Cell startTimeCell = row.getCell(5);
                if (startTimeCell != null && startTimeCell.getCellType() != CellType.BLANK) {
                    String startTimeStr = getTimeString(startTimeCell);
                    if (!startTimeStr.isEmpty()) {
                        course.setStartTime(LocalTime.parse(startTimeStr, timeFormatter));
                    }
                }

                // 7. 读取结束时间（可选，第6列）
                Cell endTimeCell = row.getCell(6);
                if (endTimeCell != null && endTimeCell.getCellType() != CellType.BLANK) {
                    String endTimeStr = getTimeString(endTimeCell);
                    if (!endTimeStr.isEmpty()) {
                        course.setEndTime(LocalTime.parse(endTimeStr, timeFormatter));
                    }
                }

                // 读取类别ID（第7列）
                Cell categoryIdCell = row.getCell(7);
                if (categoryIdCell != null && categoryIdCell.getCellType() != CellType.BLANK) {
                    if (categoryIdCell.getCellType() == CellType.NUMERIC) {
                        course.setCategoryId((long) categoryIdCell.getNumericCellValue());
                    } else if (categoryIdCell.getCellType() == CellType.STRING) {
                        try {
                            course.setCategoryId(Long.parseLong(categoryIdCell.getStringCellValue().trim()));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("类别ID格式错误，行号：" + (row.getRowNum() + 1));
                        }
                    }
                }

                courses.add(course);
            }

            // 保存所有课程（包含唯一性校验）
            return saveBatchCourses(courses);
        }
    }

    // 辅助方法：从单元格获取字符串值
    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return "";
    }

    // 辅助方法：从单元格获取时间字符串
    private String getTimeString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return LocalTime.ofNanoOfDay(
                    (long) (cell.getNumericCellValue() * 24 * 60 * 60 * 1_000_000_000)
            ).format(timeFormatter);
        }
        return "";
    }
}