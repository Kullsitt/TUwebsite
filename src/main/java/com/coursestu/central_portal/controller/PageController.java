package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.dto.TULoginResponse;
import com.coursestu.central_portal.model.Assignment;
import com.coursestu.central_portal.model.Course;
import com.coursestu.central_portal.model.Enrollment;
import com.coursestu.central_portal.model.Student;
import com.coursestu.central_portal.repository.AssignmentRepository;
import com.coursestu.central_portal.repository.CourseRepository;
import com.coursestu.central_portal.repository.EnrollmentRepository;
import com.coursestu.central_portal.repository.StudentRepository;
import com.coursestu.central_portal.service.AssignmentService;
import com.coursestu.central_portal.model.User;
import com.coursestu.central_portal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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
    // Teacher
    // ===============================

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
                                      @RequestParam(required = false) String teacherName,
                                      @RequestParam int capacity) {

        Course newCourse = new Course();
        newCourse.setCourseId(courseId);
        newCourse.setCourseName(courseName);
        newCourse.setCapacity(capacity);
        newCourse.setTeacherName(teacherName);

        // ตอนนี้ยังไม่ผูก professor จริง เพราะ login teacher ยังไม่ได้ map กับ Professor entity
        courseRepository.save(newCourse);

        return "redirect:/dashboard/teacher";
    }

    @PostMapping("/teacher/course/delete")
    public String deleteCourse(@RequestParam String courseId, HttpSession session) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        try {
            List<Assignment> courseAssignments = assignmentRepository.findByCourseCourseId(courseId);
            if (!courseAssignments.isEmpty()) {
                assignmentRepository.deleteAll(courseAssignments);
            }

            List<Enrollment> courseEnrollments = enrollmentRepository.findByCourse_CourseId(courseId);
            if (!courseEnrollments.isEmpty()) {
                enrollmentRepository.deleteAll(courseEnrollments);
            }

            courseRepository.deleteById(courseId);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public String getTeacherCourseDetailPage(@RequestParam(name = "id") String courseId,
                                             HttpSession session,
                                             Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"teacher".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);

        model.addAttribute("announcements", allItems.stream()
                .filter(a -> "announcement".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("generals", allItems.stream()
                .filter(a -> "general".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("materials", allItems.stream()
                .filter(a -> "material".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("homeworks", allItems.stream()
                .filter(a -> "homework".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("quizzes", allItems.stream()
                .filter(a -> "quizzes".equals(a.getType()))
                .collect(Collectors.toList()));

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

            // ใช้ service เพื่อให้ notification ทำงาน
            assignmentService.saveAssignment(assignment);
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
    public String deleteContent(@RequestParam Long assignmentId,
                                @RequestParam String courseId) {
        assignmentRepository.deleteById(assignmentId);
        return "redirect:/dashboard/teacher/course?id=" + courseId;
    }

    // ===============================
    // Student
    // ===============================

    @GetMapping("/dashboard/student")
    public String getDashboardPage(@RequestParam(name = "id") String courseId,
                                   HttpSession session,
                                   Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        List<Assignment> allItems = assignmentRepository.findByCourseCourseId(courseId);

        model.addAttribute("announcements", allItems.stream()
                .filter(a -> "announcement".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("generals", allItems.stream()
                .filter(a -> "general".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("materials", allItems.stream()
                .filter(a -> "material".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("homeworks", allItems.stream()
                .filter(a -> "homework".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("quizzes", allItems.stream()
                .filter(a -> "quizzes".equals(a.getType()))
                .collect(Collectors.toList()));

        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);

        return "dashboard/student/dashboardstudent";
    }

    @GetMapping("/home/student")
    public String getMyCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Student student = getOrCreateStudent(user);

        List<Enrollment> myEnrollments = enrollmentRepository.findByStudent_Id(student.getId());

        List<Enrollment> validEnrollments = myEnrollments.stream()
                .filter(e -> e.getCourse() != null)
                .collect(Collectors.toList());

        List<String> myCourseIds = validEnrollments.stream()
                .map(e -> e.getCourse().getCourseId())
                .collect(Collectors.toList());

        List<Assignment> feedItems = assignmentRepository.findAll().stream()
                .filter(item -> item.getCourse() != null)
                .filter(item -> myCourseIds.contains(item.getCourse().getCourseId()))
                .sorted(Comparator.comparing(Assignment::getId).reversed())
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("enrolledCourses", validEnrollments);
        model.addAttribute("feedItems", feedItems);

        return "home/student/my_courses";
    }

    @PostMapping("/course/enroll/confirm")
    public String confirmEnrollment(@RequestParam String courseId, HttpSession session) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Student student = getOrCreateStudent(user);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (course != null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollmentRepository.save(enrollment);
        }

        return "redirect:/home/student";
    }

    @GetMapping("/all-courses")
    public String getAllCoursesPage(HttpSession session, Model model) {
        TULoginResponse user = (TULoginResponse) session.getAttribute("user");
        if (user == null || !"student".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Student student = getOrCreateStudent(user);

        List<Course> allCourses = courseRepository.findAll();

        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(student.getId());

        List<String> enrolledCourseIds = enrollments.stream()
                .filter(e -> e.getCourse() != null)
                .map(e -> e.getCourse().getCourseId())
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

        Student student = getOrCreateStudent(user);

        List<Course> allCourses = courseRepository.findAll();

        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(student.getId());

        List<String> enrolledCourseIds = enrollments.stream()
                .filter(e -> e.getCourse() != null)
                .map(e -> e.getCourse().getCourseId())
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

    // ===============================
    // Helper
    // ===============================

    private Student getOrCreateStudent(TULoginResponse user) {
        String studentCode = user.getUsername();

        return studentRepository.findByStudentCode(studentCode)
                .orElseGet(() -> {
                    User newUser = new User();

                    String email = user.getEmail();

                    // ถ้าTU APIยังไม่ส่งemailมาใช้email testก่อน
                    if (email == null || email.isBlank()) {
                        email = "ใส่เมลที่จะลองเทส";
                    }

                    newUser.setEmail(email);
                    newUser.setPassword("");
                    newUser.setRole("STUDENT");

                    User savedUser = userRepository.save(newUser);

                    Student student = new Student();
                    student.setStudentCode(studentCode);
                    student.setFirstname(user.getDisplaynameEn() != null ? user.getDisplaynameEn() : studentCode);
                    student.setSurname("");
                    student.setFaculty(user.getFaculty() != null ? user.getFaculty() : "");
                    student.setUser(savedUser);

                    return studentRepository.save(student);
                });
    }
}