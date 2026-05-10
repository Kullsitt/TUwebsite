package com.coursestu.central_portal.service;

import com.coursestu.central_portal.model.DashboardItem;
import com.coursestu.central_portal.repository.DashboardItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardItemService {
    
    @Autowired
    private DashboardItemRepository repository;

    // Method สำหรับดึงข้อมูลทั้งหมดของรายวิชาไปแสดงหน้า Dashboard
    public List<DashboardItem> getDashboardFeed(String courseId) {
        return repository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }
    
 // Method ใหม่สำหรับบันทึกข้อมูลลงฐานข้อมูล
    public DashboardItem createDashboardItem(DashboardItem item) {
        // ให้ระบบเซ็ตเวลาปัจจุบันที่สร้างให้อัตโนมัติเลย (อาจารย์จะได้ไม่ต้องกรอกเอง)
        item.setCreatedAt(java.time.LocalDateTime.now()); 
        return repository.save(item);
    }
    
    
}