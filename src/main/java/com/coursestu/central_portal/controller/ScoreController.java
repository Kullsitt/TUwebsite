package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.model.Submission;
import com.coursestu.central_portal.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentScores(@PathVariable Long studentId) {
        
        Long authenticatedStudentId = 1L; 

        if (!studentId.equals(authenticatedStudentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Access Denied: Cannot view other student's scores.");
        }

        List<Submission> scores = submissionRepository.findByStudent_Id(studentId);
        return ResponseEntity.ok(scores);
    }
}