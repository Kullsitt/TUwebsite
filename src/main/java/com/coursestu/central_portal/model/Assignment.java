package com.coursestu.central_portal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String fileUrl; // สำหรับพาร์ท File Upload 
    private LocalDateTime deadline;
    private String fileName;

    // เชื่อมต่อกับ Course (ตาม ER ที่เป็น Weak Entity)
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; 
}