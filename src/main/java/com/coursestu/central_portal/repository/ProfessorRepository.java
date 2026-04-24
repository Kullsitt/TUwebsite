package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}