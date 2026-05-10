package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUser_Id(Long userId);
}