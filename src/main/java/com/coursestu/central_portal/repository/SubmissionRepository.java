package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent_Id(Long studentId);
    List<Submission> findByAssignment_Id(Long assignmentId);
	List<Submission> findByAssignmentId(Long id);
}