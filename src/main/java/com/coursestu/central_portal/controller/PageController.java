package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.dto.TULoginResponse;
import com.coursestu.central_portal.model.*;
import com.coursestu.central_portal.repository.*;
import com.coursestu.central_portal.service.AssignmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // 👈 เพิ่ม Import สำหรับการลบข้อมูลที่เกี่ยวข้องกัน
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PageController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AssignmentService assignmentService;

    // ===============================
    // Teacher Section
    // ===============================

    @GetMapping("/dashboard/teacher")
    public String getTeacherDashboardPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("allCourses", courseRepository.findAll());
        return "home/teacher/teacher_my_courses";
    }

    @GetMapping("/dashboard/teacher/course")
    public String getTeacherCourseDetailPage(@RequestParam(name = "id") String courseId, HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);
        model.addAttribute("announcements", allItems.stream().filter(a -> "announcement".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("generals", allItems.stream().filter(a -> "general".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("materials", allItems.stream().filter(a -> "material".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("homeworks", allItems.stream().filter(a -> "homework".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("quizzes", allItems.stream().filter(a -> "quizzes".equals(a.getType())).collect(Collectors.toList()));

        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);

        return "dashboard/teacher/dashboardteacher"; 
    }

    @PostMapping("/dashboard/teacher/add-content")
    public String addContent(@RequestParam String courseId,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String type,
                             @RequestParam(required = false) String deadline,
                             @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course != null) {
                Assignment assignment = new Assignment();
                assignment.setTitle(title);
                assignment.setDescription(description);
                assignment.setType(type);
                assignment.setCourse(course);

                if (file != null && !file.isEmpty()) {
                    String fileName = file.getOriginalFilename();
                    assignment.setFileName(fileName);
                }

                if (deadline != null && !deadline.isEmpty()) {
                    assignment.setDeadline(LocalDate.parse(deadline).atStartOfDay());
                }
                assignmentService.saveAssignment(assignment);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    
    @Autowired
    private SubmissionRepository submissionRepository; // ต้องประกาศตัวแปรนี้ไว้

    @GetMapping("/dashboard/teacher/evaluation/{id}")
    public String getEvaluationPage(@PathVariable Long id, Model model) {
        // 1. ดึงข้อมูลการส่งงานตาม ID ที่รับมา
        List<Submission> submissions = submissionRepository.findByAssignmentId(id);
        
        // 2. สมมติว่าดึงชื่อวิชาจากฐานข้อมูล (คุณอาจต้องมี CourseRepository เพื่อหาชื่อวิชาจาก id ของการบ้าน)
        // ในที่นี้ส่งค่าตัวแปรไปให้ HTML ใช้งาน
        //model.addAttribute("courseId", 1); // เปลี่ยนเลข 1 เป็น ID วิชาจริงที่ดึงมา
        //model.addAttribute("subjectCode", "CS271 ARTIFICIAL INTELLIGENCE"); // ดึงจาก DB
        //model.addAttribute("assignmentTitle", "การบ้านครั้งที่ 1");
        
        model.addAttribute("submissions", submissions);
        return "dashboard/teacher/evaluation";
    }
    

    // ===============================
    // Student Section & Helpers
    // ===============================

    @GetMapping("/home/student")
    public String getMyCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) return "redirect:/login";

        try {
            Student student = getOrCreateStudent(user);
            List<Enrollment> myEnrollments = enrollmentRepository.findByStudent_Id(student.getId());
            List<Enrollment> validEnrollments = myEnrollments.stream().filter(e -> e.getCourse() != null).collect(Collectors.toList());
            List<String> myCourseIds = validEnrollments.stream().map(e -> e.getCourse().getCourseId()).collect(Collectors.toList());

            List<Assignment> feedItems = assignmentRepository.findAll().stream()
                    .filter(item -> item.getCourse() != null)
                    .filter(item -> myCourseIds.contains(item.getCourse().getCourseId()))
                    .sorted(Comparator.comparing(Assignment::getId).reversed())
                    .collect(Collectors.toList());

            model.addAttribute("user", user);
            model.addAttribute("enrolledCourses", validEnrollments);
            model.addAttribute("feedItems", feedItems);
        } catch (Exception e) { e.printStackTrace(); }
        return "home/student/my_courses";
    }

    @GetMapping("/dashboard/student")
    public String getDashboardPage(@RequestParam(name = "id") String courseId, HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) return "redirect:/login";

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);
        model.addAttribute("announcements", allItems.stream().filter(a -> "announcement".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("generals", allItems.stream().filter(a -> "general".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("materials", allItems.stream().filter(a -> "material".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("homeworks", allItems.stream().filter(a -> "homework".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("quizzes", allItems.stream().filter(a -> "quizzes".equals(a.getType())).collect(Collectors.toList()));
        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);
        return "dashboard/student/dashboardstudent";
    }

    // 🎯 ปรับปรุง: แก้ Error 500 สำหรับอาจารย์
    @GetMapping("/all-courses")
    public String getAllCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<String> enrolledCourseIds = new ArrayList<>();

        if ("student".equals(session.getAttribute("role"))) {
            Student student = getOrCreateStudent(user);
            List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(student.getId());
            enrolledCourseIds = enrollments.stream()
                    .filter(e -> e.getCourse() != null)
                    .map(e -> e.getCourse().getCourseId())
                    .collect(Collectors.toList());
        }

        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        model.addAttribute("user", user);
        model.addAttribute("allCourses", courseRepository.findAll());
        
        return "home/student/all_courses";
    }

    // 🎯 อันนี้แหละคือจุดที่ถูกแก้: ให้เตะไปที่ /all-courses ตัวหลักเลย (ลบอันซ้ำออกให้แล้ว)
    @GetMapping("/dashboard/teacher/all-courses")
    public String getTeacherAllCoursesPage() {
        return "redirect:/all-courses"; 
    }

    @PostMapping("/course/enroll/confirm")
    public String confirmEnrollment(@RequestParam String courseId, HttpSession session) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Student student = getOrCreateStudent(user);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            boolean alreadyEnrolled = enrollmentRepository.findByStudent_Id(student.getId()).stream()
                    .anyMatch(e -> e.getCourse().getCourseId().equals(courseId));
            if (!alreadyEnrolled) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollmentRepository.save(enrollment);
            }
        }
        return "redirect:/home/student";
    }

    private Student getOrCreateStudent(TULoginResponse user) {
        String studentCode = user.getUsername();
        return studentRepository.findByStudentCode(studentCode).orElseGet(() -> {
            String email = (user.getEmail() != null && !user.getEmail().isBlank()) ? user.getEmail() : studentCode + "@tu.ac.th";
            User newUser = userRepository.findByEmail(email).orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setPassword(""); 
                u.setRole("STUDENT");
                return userRepository.save(u);
            });
            Student student = new Student();
            student.setStudentCode(studentCode);
            student.setFirstname(user.getDisplaynameEn() != null ? user.getDisplaynameEn() : studentCode);
            student.setFaculty(user.getFaculty() != null ? user.getFaculty() : "");
            student.setUser(newUser);
            return studentRepository.save(student);
        });
    }
    
    @GetMapping("/assignment/submit") 
    public String getSubmitAssignmentPage() {
        return "dashboard/student/submit"; 
    }

    // 🎯 เมธอดสำหรับรับค่าจาก Modal และสร้างวิชาใหม่
    @PostMapping("/teacher/course/save")
    public String saveCourse(@RequestParam String courseId,
                             @RequestParam String courseName,
                             @RequestParam String teacherName,
                             @RequestParam int capacity) {
        try {
            // เช็กก่อนว่ามีรหัสวิชานี้ในระบบหรือยัง จะได้ไม่ซ้ำ
            Course existingCourse = courseRepository.findById(courseId).orElse(null);
            
            if (existingCourse == null) {
                Course newCourse = new Course();
                newCourse.setCourseId(courseId);
                newCourse.setCourseName(courseName);
                newCourse.setTeacherName(teacherName);
                newCourse.setCapacity(capacity);
                
                // บันทึกลง Database
                courseRepository.save(newCourse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // เซฟเสร็จแล้วให้เด้งกลับไปหน้า Dashboard ของอาจารย์
        return "redirect:/dashboard/teacher";
    }

    // 🎯 เพิ่มเมธอดลบวิชาตรงนี้!
    @PostMapping("/teacher/course/delete")
    @Transactional // บังคับให้ทำลบข้อมูลทุกส่วนให้เสร็จพร้อมกัน ถ้าพังให้ยกเลิก
    public String deleteCourse(@RequestParam String courseId) {
        try {
            // 1. ลบเนื้อหา/การบ้าน/ประกาศ ที่ผูกกับวิชานี้ทิ้งก่อน
            List<Assignment> assignments = assignmentRepository.findByCourseCourseId(courseId);
            if (!assignments.isEmpty()) {
                assignmentRepository.deleteAll(assignments);
            }

            // 2. ลบประวัติการลงทะเบียนของนักเรียนในวิชานี้ทิ้ง
            List<Enrollment> enrollments = enrollmentRepository.findAll().stream()
                    .filter(e -> e.getCourse() != null && e.getCourse().getCourseId().equals(courseId))
                    .collect(Collectors.toList());
            if (!enrollments.isEmpty()) {
                enrollmentRepository.deleteAll(enrollments);
            }

            // 3. เมื่อเคลียร์ของที่ผูกติดหมดแล้ว ก็ทำการลบตัวรายวิชาทิ้งได้เลย
            courseRepository.deleteById(courseId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // ลบเสร็จแล้วให้เด้งกลับมาหน้า Dashboard
        return "redirect:/dashboard/teacher";
    }
 // 🎯 เพิ่มเมธอดสำหรับรับหน้า "ผลคะแนนของฉัน"
    @GetMapping("/score-report")
    public String getScoreReportPage(HttpSession session, Model model) {
        // เช็กก่อนว่าใช่นักศึกษาล็อกอินอยู่ไหม
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        
        // ส่งข้อมูล user ไปให้หน้าเว็บแสดงชื่อโปรไฟล์
        model.addAttribute("user", user);
        
        // ⚠️ สำคัญ: ตรงนี้ต้องชี้ไปที่ไฟล์ HTML ที่เพื่อนคุณสร้างไว้
        // สมมติว่าเพื่อนเซฟไว้ที่ src/main/resources/templates/home/student/score_report.html
        return "home/student/score_report"; 
    }
}