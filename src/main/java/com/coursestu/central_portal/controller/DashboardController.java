package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.DashboardItem;
import com.coursestu.central_portal.service.DashboardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardItemService dashboardItemService;

    // API สำหรับดึงข้อมูล (ดักจับ Error กรณีพัง)
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseDashboard(@PathVariable String courseId) {
        try {
            // ลองดึงข้อมูลตามปกติ
            return ResponseEntity.ok(dashboardItemService.getDashboardFeed(courseId));
        } catch (Exception e) {
            // ถ้าข้อมูลแสดงผิดเพี้ยน หรือ Database มีปัญหา จะพ่นคำนี้ออกไปแทนตัวแดงๆ
            return ResponseEntity.status(500).body("ขออภัยข้อมูลผิดพลาด");
        }
    }

    // API สำหรับเพิ่มข้อมูล (ตรวจสอบความถูกต้องก่อนเซฟ)
    @PostMapping("/create")
    public ResponseEntity<?> createItem(@RequestBody DashboardItem item) {
        try {
            // ตรวจสอบข้อมูลถูกต้อง: เช็คว่าลืมใส่หัวข้อมาหรือไม่
            if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ขออภัยข้อมูลผิดพลาด: กรุณาระบุหัวข้อประกาศ");
            }
            
            // ถ้าข้อมูลครบถ้วน ให้บันทึกตามปกติ
            DashboardItem savedItem = dashboardItemService.createDashboardItem(item);
            return ResponseEntity.ok(savedItem);
            
        } catch (Exception e) {
            // ดักจับ Error ทั่วไป
            return ResponseEntity.status(500).body("ขออภัยข้อมูลผิดพลาด");
        }
    }
}