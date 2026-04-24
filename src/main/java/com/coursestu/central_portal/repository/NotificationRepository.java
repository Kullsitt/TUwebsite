package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCourse_CourseId(String courseId);
}