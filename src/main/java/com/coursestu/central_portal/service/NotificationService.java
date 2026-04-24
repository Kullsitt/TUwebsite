package com.coursestu.central_portal.service;

import com.coursestu.central_portal.model.*;
import com.coursestu.central_portal.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final EnrollmentRepository enrollmentRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserNotificationRepository userNotificationRepository,
            EnrollmentRepository enrollmentRepository,
            EmailService emailService) {

        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.emailService = emailService;
    }

    public void notifyStudentsInCourse(Course course, String title, String message, String type) {

        Notification notification = new Notification();
        notification.setCourse(course);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);

        Notification savedNotification = notificationRepository.save(notification);

        List<Enrollment> enrollments =
                enrollmentRepository.findByCourse_CourseId(course.getCourseId());

        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();

            if (student != null && student.getUser() != null) {
                UserNotification userNotification = new UserNotification();
                userNotification.setUser(student.getUser());
                userNotification.setNotification(savedNotification);
                userNotification.setReadStatus(false);

                userNotificationRepository.save(userNotification);

                String email = student.getUser().getEmail();

                if (email != null && !email.isBlank()) {
                    String html = buildEmailHtml(course, title, message, type);

                    emailService.sendHtmlEmail(
                            email,
                            "[" + course.getCourseId() + "] " + title,
                            html
                    );
                }
            }
        }
    }

    private String buildEmailHtml(Course course, String title, String message, String type) {
        String courseId = course.getCourseId();
        String courseName = course.getCourseName() != null ? course.getCourseName() : "";
        String portalLink = "http://localhost:8080/dashboard/student?id=" + courseId;

        return """
                <div style="font-family: Arial, sans-serif; background:#f6f6f6; padding:24px;">
                    <div style="max-width:600px; margin:auto; background:white; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                        <div style="background:#f39c12; color:white; padding:20px;">
                            <h2 style="margin:0;">Thammasat Central Portal</h2>
                            <p style="margin:6px 0 0 0;">New %s Notification</p>
                        </div>

                        <div style="padding:24px; color:#333;">
                            <h3 style="margin-top:0;">%s</h3>

                            <p><strong>Course:</strong> %s %s</p>
                            <p><strong>Type:</strong> %s</p>

                            <div style="background:#fafafa; padding:16px; border-left:4px solid #f39c12; margin:18px 0;">
                                %s
                            </div>

                            <a href="%s"
                               style="display:inline-block; background:#f39c12; color:white; padding:12px 18px; text-decoration:none; border-radius:6px;">
                                View in Portal
                            </a>
                        </div>

                        <div style="padding:16px; font-size:12px; color:#888; text-align:center; border-top:1px solid #eee;">
                            This email was sent automatically by Thammasat Central Portal.
                        </div>
                    </div>
                </div>
                """.formatted(
                escapeHtml(type),
                escapeHtml(title),
                escapeHtml(courseId),
                escapeHtml(courseName),
                escapeHtml(type),
                escapeHtml(message),
                portalLink
        );
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}