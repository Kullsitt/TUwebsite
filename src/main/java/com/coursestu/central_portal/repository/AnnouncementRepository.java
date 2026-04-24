package com.coursestu.central_portal.repository;

import com.coursestu.central_portal.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourse_CourseId(String courseId);
}