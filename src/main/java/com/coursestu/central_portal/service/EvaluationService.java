package com.coursestu.central_portal.service;

import com.coursestu.central_portal.model.Submission;
import com.coursestu.central_portal.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationService {

    @Autowired
    private SubmissionRepository submissionRepository;

    public void validateEvaluation(int score) throws Exception {
        if (score < 0) throw new Exception("คะแนนห้ามติดลบ");
        if (score > 100) throw new Exception("คะแนนห้ามเกิน 100");
    }

    // ✅ Method ที่ EvaluationApiController เรียกใช้
    public void saveStudentScore(String assignmentId, String studentId, Double score) throws Exception {
        if (score < 0 || score > 100) {
            throw new Exception("คะแนนต้องอยู่ระหว่าง 0 - 100");
        }

        // หา Submission ที่ตรงกับ assignmentId + studentId
        Submission submission = submissionRepository
            .findByAssignment_Id(Long.parseLong(assignmentId))
            .stream()
            .filter(s -> s.getStudent().getId().equals(Long.parseLong(studentId)))
            .findFirst()
            .orElseThrow(() -> new Exception("ไม่พบข้อมูลการส่งงานของนักศึกษานี้"));

        submission.setScore(score);
        submissionRepository.save(submission);
    }
}