package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    // ตรงนี้ปล่อยว่างไว้ได้เลยครับ Spring Data JPA จะสร้างคำสั่ง Save, Update, Delete ให้เราอัตโนมัติ
}