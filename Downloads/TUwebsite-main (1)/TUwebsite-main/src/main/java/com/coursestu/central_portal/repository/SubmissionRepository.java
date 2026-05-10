package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent_Id(Long studentId);
    List<Submission> findByAssignment_Id(Long assignmentId);
    List<Submission> findByAssignmentId(Long id);
    List<Submission> findByStudent_IdAndAssignment_Course_CourseId(Long studentId, String courseId);
}