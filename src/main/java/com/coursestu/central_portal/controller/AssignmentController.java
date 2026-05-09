package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.Assignment;
import com.coursestu.central_portal.model.Course;
import com.coursestu.central_portal.model.Student;
import com.coursestu.central_portal.model.Submission;
import com.coursestu.central_portal.repository.AssignmentRepository;
import com.coursestu.central_portal.repository.StudentRepository;
import com.coursestu.central_portal.repository.SubmissionRepository;
import com.coursestu.central_portal.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private JavaMailSender mailSender;

    // 🚩 1. นำเข้า Repository ที่ต้องใช้สำหรับการบันทึกการส่งงาน
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    // 📁 Path สำหรับเก็บไฟล์ (แยกโฟลเดอร์กัน)
    private final String UPLOAD_DIR = "uploads/";
    private final String SUBMISSION_DIR = "uploads/submissions/";

    /**
     * 1. สำหรับ "อาจารย์" สร้างโจทย์การบ้าน
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Assignment createAssignment(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("deadline") String deadline,
            @RequestParam("courseId") String courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        // 🔧 สร้าง folder
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }

        // 🔧 บันทึกไฟล์โจทย์
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);

        try {
            assignment.setDeadline(LocalDateTime.parse(deadline));
        } catch (DateTimeParseException e) {
            assignment.setDeadline(LocalDateTime.now());
        }

        assignment.setFileName(fileName);
        String fileViewUrl = "http://localhost:8080/documents/view/" + fileName;
        assignment.setFileUrl(fileViewUrl);

        Course course = new Course();
        course.setCourseId(courseId);
        assignment.setCourse(course);

        Assignment savedAssignment = assignmentService.saveAssignment(assignment);

        // 🔧 ส่ง Email (Optional)
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com");
            message.setTo("target-email@gmail.com");
            message.setSubject("New Assignment: " + title);
            message.setText("Course: " + courseId + "\nView: " + fileViewUrl);
            mailSender.send(message);
        } catch (Exception e) { System.err.println("Mail error: " + e.getMessage()); }

        return savedAssignment;
    }

    /**
     * 2. 🔥 สำหรับ "นักศึกษา" ส่งการบ้าน
     */
    @PostMapping(value = "/submit/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView submitAssignment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("assignmentId") Long assignmentId, 
            @RequestParam("studentId") String studentId,
            @RequestParam("courseId") String courseId,
            RedirectAttributes attributes) {

        try {
            if (file.isEmpty()) {
                return new RedirectView("/assignment/submithw?assignmentId=" + assignmentId + "&courseId=" + courseId);
            }

            Path submissionPath = Paths.get(SUBMISSION_DIR);
            if (!Files.exists(submissionPath)) { Files.createDirectories(submissionPath); }

            String assignmentIdStr = String.valueOf(assignmentId); 
            String fileName = studentId + "_ID" + assignmentIdStr + "_" + file.getOriginalFilename();
            
            Path filePath = submissionPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 🚩 2. บันทึกข้อมูลการส่งงานลง Database (เพื่อให้โผล่หน้าอาจารย์)
            Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
            Student student = studentRepository.findByStudentCode(studentId).orElse(null);

            if (assignment != null && student != null) {
                // เช็คว่าเคยส่งงานนี้หรือยัง ถ้าเคยส่งแล้วให้อัปเดตไฟล์เดิม (กันแถวซ้ำ)
                List<Submission> existingSubmissions = submissionRepository.findByStudent_IdAndAssignment_Course_CourseId(student.getId(), courseId);
                Submission submission = existingSubmissions.stream()
                        .filter(s -> s.getAssignment().getId().equals(assignmentId))
                        .findFirst()
                        .orElse(new Submission());

                submission.setAssignment(assignment);
                submission.setStudent(student);
                submission.setFileName(fileName);
                submission.setFileUrl("/api/assignments/download/submission/" + fileName); // ชี้ไปที่ API โหลดงานนักศึกษา
                submission.setSubmittedAt(LocalDateTime.now());
                
                submissionRepository.save(submission); 
                System.out.println("✅ เซฟข้อมูลลง Database สำเร็จ! รหัสนักศึกษา: " + studentId);
            } else {
                System.out.println("❌ ไม่สามารถเซฟลง DB ได้ เนื่องจากไม่พบ Assignment หรือ Student");
            }

            return new RedirectView("/dashboard/student?id=" + courseId + "&success=true");

        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("/assignment/submithw?assignmentId=" + assignmentId + "&courseId=" + courseId + "&error=server_error");
        }
    }

    /**
     * 3. ดาวน์โหลดไฟล์ "โจทย์การบ้าน" (ของอาจารย์)
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🚩 4. เพิ่มใหม่: ดาวน์โหลดไฟล์ "งานที่นักศึกษาส่ง"
     * (เพราะมันเก็บอยู่คนละโฟลเดอร์ คือ uploads/submissions/)
     */
    @GetMapping("/download/submission/{fileName:.+}")
    public ResponseEntity<Resource> downloadSubmissionFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(SUBMISSION_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}