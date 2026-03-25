package com.coursestu.central_portal.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {
    
    @Id
    private String courseId;
    private String courseName;
    
    // --- ตัวแปรที่เพิ่มเข้ามาสำหรับโชว์หน้าเว็บ ---
    private String teacherName; 
    private int capacity;       

    // ความสัมพันธ์ที่คุณสร้างไว้เดิม
    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments;

    // =========================================
    // Getters and Setters (เขียนเองชัวร์กว่า @Data)
    // =========================================
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<Assignment> getAssignments() { return assignments; }
    public void setAssignments(List<Assignment> assignments) { this.assignments = assignments; }
}