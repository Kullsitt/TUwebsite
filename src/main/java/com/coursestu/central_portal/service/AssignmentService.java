package com.coursestu.central_portal.service;

import com.coursestu.central_portal.model.Assignment;
import com.coursestu.central_portal.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private NotificationService notificationService;

    public Assignment saveAssignment(Assignment assignment) {
        Assignment savedAssignment = assignmentRepository.save(assignment);

        if (savedAssignment.getCourse() != null) {
            notificationService.notifyStudentsInCourse(
                    savedAssignment.getCourse(),
                    "New Assignment: " + savedAssignment.getTitle(),
                    savedAssignment.getDescription(),
                    "ASSIGNMENT"
            );
        }

        return savedAssignment;
    }
}
