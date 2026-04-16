package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.Course;
import com.coursestu.central_portal.model.DashboardItem;
import com.coursestu.central_portal.model.Enrollment;
import com.coursestu.central_portal.repository.CourseRepository;
import com.coursestu.central_portal.repository.DashboardItemRepository;
import com.coursestu.central_portal.repository.EnrollmentRepository;
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

    // ----- [เพิ่มใหม่] นำ CourseRepository มาใช้สำหรับหาข้อมูลอาจารย์ -----
    @Autowired
    private CourseRepository courseRepository;

    // ==========================================
    // API สำหรับดึงแจ้งเตือนของ "นักศึกษา"
    // ==========================================
    @GetMapping("/student/{studentId}")
    public List<DashboardItem> getStudentNotifications(@PathVariable String studentId) {
        // 1. หาวิชาทั้งหมดที่นักศึกษาคนนี้ลงเรียน
        List<String> myCourses = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toList());

        // ถ้าไม่ได้ลงวิชาอะไรเลย ส่งลิสต์ว่างกลับไป
        if (myCourses.isEmpty()) {
            return List.of();
        }

        // 2. ดึงประกาศ/งานทั้งหมดที่อยู่ในวิชาที่เรียน เรียงจากใหม่ไปเก่า (ดึงมาแค่ 10 อันล่าสุด)
        return dashboardItemRepository.findByCourseIdInOrderByCreatedAtDesc(myCourses)
                .stream().limit(10).collect(Collectors.toList());
    }

    // ==========================================
    // API สำหรับดึงแจ้งเตือนของ "อาจารย์" (ที่เพิ่มเข้ามาใหม่)
    // ==========================================
    @GetMapping("/teacher/{teacherName}")
    public List<DashboardItem> getTeacherNotifications(@PathVariable String teacherName) {
        // 1. หาวิชาทั้งหมดที่อาจารย์คนนี้เป็นผู้สอน
        List<String> myCourses = courseRepository.findByTeacherName(teacherName)
                .stream()
                .map(Course::getCourseId)
                .collect(Collectors.toList());

        // ถ้าไม่ได้สอนวิชาอะไรเลย ส่งลิสต์ว่างกลับไป
        if (myCourses.isEmpty()) {
            return List.of();
        }

        // 2. ดึงประกาศ/งานทั้งหมดที่อยู่ในวิชาที่สอน เรียงจากใหม่ไปเก่า (ดึงมาแค่ 10 อันล่าสุด)
        return dashboardItemRepository.findByCourseIdInOrderByCreatedAtDesc(myCourses)
                .stream().limit(10).collect(Collectors.toList());
    }
}