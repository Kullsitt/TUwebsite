package com.coursestu.central_portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentId;
    private Integer taskId;
    private Integer evaluationScore;
    private String feedback;

    // Getter และ Setter (ถ้าไม่มี @Data ต้องมีพวกนี้ครับ)
    public Integer getEvaluationScore() { return evaluationScore; }
    public void setEvaluationScore(Integer score) { this.evaluationScore = score; }
}