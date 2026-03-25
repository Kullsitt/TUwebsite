package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.dto.TULoginResponse;
import com.coursestu.central_portal.model.Assignment;
import com.coursestu.central_portal.model.Course; 
import com.coursestu.central_portal.model.Enrollment; 
import com.coursestu.central_portal.repository.AssignmentRepository;
import com.coursestu.central_portal.repository.CourseRepository; 
import com.coursestu.central_portal.repository.EnrollmentRepository; 
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List; 
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    // ==========================================
    // ส่วนที่ 1: ระบบ Dashboard สำหรับอาจารย์
    // ==========================================

    @GetMapping("/dashboard/teacher")
    public String getTeacherDashboardPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Course> allCourses = courseRepository.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("allCourses", allCourses); 
        return "home/teacher/teacher_my_courses"; 
    }

    @PostMapping("/teacher/course/save")
    public String saveCourseFromModal(@RequestParam String courseId, 
                                      @RequestParam String courseName,
                                      @RequestParam String teacherName,
                                      @RequestParam int capacity) {
        
        Course newCourse = new Course();
        newCourse.setCourseId(courseId);
        newCourse.setCourseName(courseName);
        newCourse.setTeacherName(teacherName);
        newCourse.setCapacity(capacity);

        courseRepository.save(newCourse);
        return "redirect:/dashboard/teacher";
    }

    // 🎯 เมธอดลบรายวิชาออกจากฐานข้อมูล
    @PostMapping("/teacher/course/delete")
    public String deleteCourse(@RequestParam String courseId, HttpSession session) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // 💾 สั่งลบวิชาจาก Database
        courseRepository.deleteById(courseId);

        // ลบเสร็จแล้วเด้งกลับไปหน้า Dashboard อาจารย์
        return "redirect:/dashboard/teacher";
    }

    @GetMapping("/dashboard/teacher/all-courses")
    public String getTeacherAllCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Course> allCourses = courseRepository.findAll();
        model.addAttribute("allCourses", allCourses);
        
        model.addAttribute("user", user);
        return "home/teacher/teacher_all_courses"; 
    }

    @GetMapping("/dashboard/teacher/course")
    public String getTeacherCourseDetailPage(@RequestParam(name="id") String courseId, HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);

        model.addAttribute("announcements", allItems.stream().filter(a -> "announcement".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("generals",      allItems.stream().filter(a -> "general".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("materials",     allItems.stream().filter(a -> "material".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("homeworks",     allItems.stream().filter(a -> "homework".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("quizzes",       allItems.stream().filter(a -> "quizzes".equals(a.getType())).collect(Collectors.toList()));

        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);
        return "dashboard/teacher/dashboardteacher"; 
    }

    @PostMapping("/dashboard/teacher/add-content")
    public String addContent(@RequestParam String courseId,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String type,
                             @RequestParam(required = false) String deadline) {
        
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            Assignment assignment = new Assignment();
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setType(type);
            assignment.setCourse(course);

            if (deadline != null && !deadline.isEmpty()) {
                assignment.setDeadline(LocalDate.parse(deadline).atStartOfDay());
            }

            assignmentRepository.save(assignment);
        }
        
        return "redirect:/dashboard/teacher/course?id=" + courseId;
    }

    @PostMapping("/dashboard/teacher/edit-content")
    public String editContent(@RequestParam Long assignmentId,
                              @RequestParam String courseId,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String type) {
        
        Assignment existing = assignmentRepository.findById(assignmentId).orElse(null);
        if (existing != null) {
            existing.setTitle(title);
            existing.setDescription(description);
            existing.setType(type);
            assignmentRepository.save(existing);
        }
        
        return "redirect:/dashboard/teacher/course?id=" + courseId;
    }

    @PostMapping("/dashboard/teacher/delete-content")
    public String deleteContent(@RequestParam Long assignmentId, @RequestParam String courseId) {
        assignmentRepository.deleteById(assignmentId);
        return "redirect:/dashboard/teacher/course?id=" + courseId;
    }

    // ==========================================
    // ส่วนที่ 2: ระบบของนักศึกษา
    // ==========================================

    @GetMapping("/dashboard/student")
    public String getDashboardPage(@RequestParam(name="id") String courseId, HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);

        model.addAttribute("announcements", allItems.stream().filter(a -> "announcement".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("generals",      allItems.stream().filter(a -> "general".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("materials",     allItems.stream().filter(a -> "material".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("homeworks",     allItems.stream().filter(a -> "homework".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("quizzes",       allItems.stream().filter(a -> "quizzes".equals(a.getType())).collect(Collectors.toList()));

        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);
        
        return "dashboard/student/dashboardstudent";
    }

    // 🎯 แก้ไขใหม่: กรองวิชาที่อาจารย์ลบทิ้งไปแล้ว ไม่ให้โชว์ในหน้านักศึกษา
    @GetMapping("/home/student")
    public String getMyCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // 1. ดึงข้อมูลการลงทะเบียนทั้งหมดของนักศึกษา
        List<Enrollment> myEnrollments = enrollmentRepository.findByStudentId(user.getUsername());
        
        // 2. ดึงข้อมูลรายวิชาทั้งหมดที่มีอยู่ในระบบตอนนี้ (ที่ยังไม่ถูกลบ)
        List<Course> activeCourses = courseRepository.findAll();
        List<String> activeCourseIds = activeCourses.stream()
                                                    .map(Course::getCourseId)
                                                    .collect(Collectors.toList());

        // 3. กรองเอาเฉพาะการลงทะเบียนที่ "รหัสวิชายังมีอยู่ในระบบ"
        List<Enrollment> validEnrollments = myEnrollments.stream()
                .filter(enrollment -> activeCourseIds.contains(enrollment.getCourseId()))
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        
        // 4. ส่งวิชาที่ valid (รอดชีวิต) ไปโชว์หน้าเว็บ
        model.addAttribute("enrolledCourses", validEnrollments); 
        
        return "home/student/my_courses"; 
    }

    @PostMapping("/course/enroll/confirm")
    public String confirmEnrollment(@RequestParam String courseId, HttpSession session) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(user.getUsername());
        enrollment.setCourseId(courseId);
        enrollmentRepository.save(enrollment);

        return "redirect:/home/student";
    }

    @GetMapping("/all-courses")
    public String getAllCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Course> allCourses = courseRepository.findAll();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(user.getUsername());
        List<String> enrolledCourseIds = enrollments.stream()
                                                    .map(Enrollment::getCourseId)
                                                    .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("allCourses", allCourses); 
        model.addAttribute("enrolledCourseIds", enrolledCourseIds); 
        
        return "home/student/all_courses"; 
    }
    
    @GetMapping("/all-courses-p2")
    public String getAllCoursesPage2(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        
        List<Course> allCourses = courseRepository.findAll();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(user.getUsername());
        List<String> enrolledCourseIds = enrollments.stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("allCourses", allCourses); 
        model.addAttribute("enrolledCourseIds", enrolledCourseIds); 
        return "home/student/all_courses_p2"; 
    }

    @GetMapping("/assignment/submit") 
    public String getSubmitAssignmentPage() {
        return "dashboard/student/submit"; 
    } 
}