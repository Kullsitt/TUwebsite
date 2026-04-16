package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.DashboardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DashboardItemRepository extends JpaRepository<DashboardItem, Long> {
    // ของเดิม: ดึงวิชาเดียว
    List<DashboardItem> findByCourseIdOrderByCreatedAtDesc(String courseId);
    
    // ของใหม่ (เพิ่มบรรทัดนี้): ดึงหลายๆ วิชาพร้อมกัน สำหรับหน้า Home 
    List<DashboardItem> findByCourseIdInOrderByCreatedAtDesc(List<String> courseIds);
}