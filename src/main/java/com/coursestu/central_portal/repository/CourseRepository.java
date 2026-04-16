package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // หาว่าอาจารย์ชื่อนี้ สอนวิชาอะไรบ้าง
    List<Course> findByTeacherName(String teacherName);
}