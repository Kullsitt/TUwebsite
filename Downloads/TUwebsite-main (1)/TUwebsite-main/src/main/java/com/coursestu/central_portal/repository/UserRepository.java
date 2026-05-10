package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 🎯 เพิ่มบรรทัดนี้เพื่อให้ PageController ค้นหาผู้ใช้จาก Email ได้ครับ
    Optional<User> findByEmail(String email);
}