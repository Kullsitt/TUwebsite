package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // ใช้หารหัสวิชาทั้งหมดที่นักศึกษาคนนี้ลงทะเบียนไว้
    List<Enrollment> findByStudentId(String studentId);
}