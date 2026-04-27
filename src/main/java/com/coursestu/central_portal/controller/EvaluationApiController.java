package com.coursestu.central_portal.controller;

// เพิ่ม 2 บรรทัดนี้เข้าไปครับ
import com.coursestu.central_portal.service.EvaluationService;
import com.coursestu.central_portal.model.Evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationApiController {

    @Autowired
    private EvaluationService evaluationService; // ตอนนี้มันควรจะหายแดงแล้ว

    @PostMapping("/save")
    public ResponseEntity<?> saveEvaluation(@RequestBody Map<String, Object> payload) {
        try {
            int score = Integer.parseInt(payload.get("evaluation_score").toString());
            evaluationService.validateEvaluation(score);
            return ResponseEntity.ok().body(Map.of("success", true, "message", "บันทึกสำเร็จ"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}