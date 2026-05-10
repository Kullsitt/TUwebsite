package com.coursestu.central_portal.model; 

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_forms")
@Data 
public class ContentForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "QUIZ", "POST", "ASSIGNMENT"
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private String fileUrl; // เก็บที่อยู่ไฟล์
    private LocalDateTime deadline;
    private LocalDateTime createdAt = LocalDateTime.now();
}