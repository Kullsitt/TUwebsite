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
    private String type;        // ✅ เพิ่มตัวนี้: เก็บประเภท (Announcement, Material, Homework, Quizzes)
    private String fileUrl; 
    private LocalDateTime deadline;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // --- Getters and Setters (เช็กชื่อเมธอดให้ตรงนะครับ) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; } // ✅ แก้จาก title2 เป็นการเซฟค่าจริง

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; } // ✅ เพิ่ม Getter สำหรับประเภท
    public void setType(String type) { this.type = type; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}