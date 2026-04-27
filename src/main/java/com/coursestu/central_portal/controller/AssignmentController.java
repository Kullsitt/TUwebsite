package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.Assignment;
import com.coursestu.central_portal.model.Course;
import com.coursestu.central_portal.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Assignment createAssignment(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("deadline") String deadline,
            @RequestParam("courseId") String courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        // 🔧 1. สร้าง folder ถ้ายังไม่มี
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 🔧 2. บันทึกไฟล์
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 🔧 3. สร้าง Assignment
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);

        try {
            assignment.setDeadline(LocalDateTime.parse(deadline));
        } catch (DateTimeParseException e) {
            assignment.setDeadline(LocalDateTime.now());
        }

        assignment.setFileName(fileName);

        // 🔥🔥🔥 ตรงนี้คือจุดสำคัญ (แก้จาก download → view)
        String fileViewUrl = "http://localhost:8080/documents/view/" + fileName;
        assignment.setFileUrl(fileViewUrl);

        Course course = new Course();
        course.setCourseId(courseId);
        assignment.setCourse(course);

        // 🔧 4. Save DB
        Assignment savedAssignment = assignmentService.saveAssignment(assignment);

        // 🔧 5. ส่ง Email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com");
            message.setTo("target-email@gmail.com");
            message.setSubject("New Assignment Created: " + title);

            message.setText(
                    "Hello,\n\nA new assignment has been uploaded.\n\n" +
                    "Course ID: " + courseId + "\n" +
                    "Title: " + title + "\n" +
                    "Deadline: " + deadline + "\n\n" +
                    "View file here: " + fileViewUrl
            );

            mailSender.send(message);
            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return savedAssignment;
    }

    // 🔽 (ยังเก็บไว้ได้ เผื่ออยากโหลดจริง)
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

