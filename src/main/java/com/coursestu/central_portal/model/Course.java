package com.coursestu.central_portal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Course {
    @Id
    private String courseId; 
    private String courseName;

    // เชื่อมกลับมายัง Assignment (1 วิชา มีได้หลายงาน)
    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments;
}