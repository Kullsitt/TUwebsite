package com.coursestu.central_portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String type;        // สำหรับแยกหมวดหมู่ (announcement, material, etc.)
    private String fileName;    // 🎯 เพิ่มตัวนี้: เก็บชื่อไฟล์ต้นฉบับ (เช่น "slide_chap1.pdf")
    private String fileUrl;     // 🎯 เพิ่มตัวนี้: เก็บลิงก์สำหรับโหลดจาก AWS S3
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFileName() { return fileName; } // ✅ Getter ที่ระบบเรียกหา
    public void setFileName(String fileName) { this.fileName = fileName; } // ✅ Setter ที่ระบบเรียกหา

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}