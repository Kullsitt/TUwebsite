package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}