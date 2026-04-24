package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.Course;
import com.coursestu.central_portal.model.DashboardItem;
import com.coursestu.central_portal.model.Enrollment;
import com.coursestu.central_portal.model.Student;
import com.coursestu.central_portal.repository.CourseRepository;
import com.coursestu.central_portal.repository.DashboardItemRepository;
import com.coursestu.central_portal.repository.EnrollmentRepository;
import com.coursestu.central_portal.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private DashboardItemRepository dashboardItemRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ==========================================
    // API สำหรับดึงแจ้งเตือนของนักศึกษา
    // ==========================================
    @GetMapping("/student/{studentId}")
    public List<DashboardItem> getStudentNotifications(@PathVariable String studentId) {

        Student student = studentRepository.findByStudentCode(studentId).orElse(null);

        if (student == null) {
            return List.of();
        }

        List<String> myCourses = enrollmentRepository.findByStudent_Id(student.getId())
                .stream()
                .filter(e -> e.getCourse() != null)
                .map(e -> e.getCourse().getCourseId())
                .collect(Collectors.toList());

        if (myCourses.isEmpty()) {
            return List.of();
        }

        return dashboardItemRepository.findByCourseIdInOrderByCreatedAtDesc(myCourses)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // ==========================================
    // API สำหรับดึงแจ้งเตือนของอาจารย์
    // ตอนนี้ใช้ findAll() ชั่วคราว เพราะยังไม่ได้ map teacherName -> Professor
    // ==========================================
    @GetMapping("/teacher/{teacherName}")
    public List<DashboardItem> getTeacherNotifications(@PathVariable String teacherName) {

        List<String> myCourses = courseRepository.findAll()
                .stream()
                .map(Course::getCourseId)
                .collect(Collectors.toList());

        if (myCourses.isEmpty()) {
            return List.of();
        }

        return dashboardItemRepository.findByCourseIdInOrderByCreatedAtDesc(myCourses)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}