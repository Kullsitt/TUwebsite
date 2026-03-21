package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.DashboardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DashboardItemRepository extends JpaRepository<DashboardItem, Long> {
    // โค้ดบรรทัดนี้คือเวทมนตร์! มันจะสร้างคำสั่งดึงข้อมูลของวิชานั้นๆ และเรียงจากใหม่ไปเก่าให้อัตโนมัติ
    List<DashboardItem> findByCourseIdOrderByCreatedAtDesc(String courseId);
}