package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // หา course ทั้งหมดที่ professor คนนี้สอน
    List<Course> findByProfessor_Id(Long professorId);

}