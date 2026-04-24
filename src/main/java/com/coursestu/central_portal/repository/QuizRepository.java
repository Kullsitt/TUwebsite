package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse_CourseId(String courseId);
}