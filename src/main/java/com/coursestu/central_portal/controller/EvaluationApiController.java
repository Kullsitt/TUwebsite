package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.coursestu.central_portal.service.EvaluationService;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationApiController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/save")
    public ResponseEntity<?> saveEvaluation(@RequestBody Map<String, Object> payload) {
        try {
            // 1. ดึงค่าจาก JSON Payload ที่ส่งมาจาก JavaScript
            String assignmentId = payload.get("assignmentId").toString();
            String studentId = payload.get("studentId").toString();
            
            // 2. แปลงคะแนนเป็น Double เพื่อให้ตรงกับ Model Submission
            Double score = Double.parseDouble(payload.get("score").toString());

            // 3. เรียก Service เพื่อบันทึกคะแนนลงฐานข้อมูล
            evaluationService.saveStudentScore(assignmentId, studentId, score);

            return ResponseEntity.ok().body(Map.of(
                "success", true, 
                "message", "บันทึกคะแนนเรียบร้อยแล้ว"
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "รูปแบบคะแนนไม่ถูกต้อง กรุณากรอกเป็นตัวเลข"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "เกิดข้อผิดพลาด: " + e.getMessage()
            ));
        }
    }
}