package com.coursestu.central_portal.model;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data 
public class AssignmentRequest {
    private String title;
    private String description;
    private String deadline;
    private String courseId;
    private MultipartFile file;
}