package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // ดึงรายชื่อวิชาทั้งหมดที่นักศึกษาคนนี้ลงทะเบียนไว้
    List<Enrollment> findByStudentId(String studentId);
}