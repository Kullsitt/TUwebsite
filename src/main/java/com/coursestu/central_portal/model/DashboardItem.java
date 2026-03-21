package com.coursestu.central_portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity

public class DashboardItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseId;     // รหัสวิชา เช่น "CS232"
    private String title;        // หัวข้อ (เช่น "ประกาศหยุดเรียน", "การบ้านครั้งที่ 1")
    
    @Column(columnDefinition = "Text")
    private String detail;       // รายละเอียด
    
    private String itemType;     // ประเภท: "ANNOUNCEMENT", "ASSIGNMENT", หรือ "QUIZ"
    
    private String fileUrl;
    
    private LocalDateTime dueDate;   // วันกำหนดส่ง (ถ้าเป็นประกาศ ปล่อยว่างได้)
    private LocalDateTime createdAt; // สร้างเมื่อไหร่
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public LocalDateTime getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}