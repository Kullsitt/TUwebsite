package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // 🎯 ดึงเนื้อหาทั้งหมดที่อยู่ในวิชานี้
    List<Assignment> findByCourseCourseId(String courseId);
}