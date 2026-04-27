package com.coursestu.central_portal.service;

import org.springframework.stereotype.Service;

@Service
public class EvaluationService {
    public void validateEvaluation(int score) throws Exception {
        if (score < 0) throw new Exception("คะแนนห้ามติดลบ");
        if (score > 100) throw new Exception("คะแนนห้ามเกิน 100");
    }
}