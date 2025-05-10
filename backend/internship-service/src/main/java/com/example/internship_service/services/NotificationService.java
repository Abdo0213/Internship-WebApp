package com.example.internship_service.services;

import com.example.internship_service.model.Notification;
import com.example.internship_service.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications for a specific student
    public Page<Notification> getNotificationsByStudentId(Long studentId, Pageable pageable) {
        return notificationRepository.findByStudentId(studentId, pageable);
    }

    // Get a single notification by ID (with student validation)
    public Optional<Notification> getNotificationByIdAndStudentId(Long id, Long studentId) {
        return notificationRepository.findByIdAndStudentId(id, studentId);
    }

    // Mark notification as read
    public Notification markAsRead(Long id, Long studentId) {
        return notificationRepository.findByIdAndStudentId(id, studentId)
                .map(notification -> {
                    notification.setRead(true);
                    return notificationRepository.save(notification);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found or doesn't belong to student"));
    }

    // Create a new notification (typically called when application status changes)
    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    // Search notifications by internship title
    public Page<Notification> searchByInternshipTitle(Long studentId, String searchTerm, Pageable pageable) {
        return notificationRepository.findByStudentIdAndInternshipTitleContainingIgnoreCase(
                studentId, searchTerm, pageable);
    }
    @Transactional
    public void deleteNotificationsByApplicationIds(List<Long> applicationIds) {
        notificationRepository.deleteAllByApplicationIds(applicationIds);
    }

}