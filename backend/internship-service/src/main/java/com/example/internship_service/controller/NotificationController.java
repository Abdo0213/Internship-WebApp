package com.example.internship_service.controller;

import com.example.internship_service.annotation.JwtValidation;
import com.example.internship_service.model.Notification;
import com.example.internship_service.services.NotificationService;
import com.example.internship_service.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    // Get all notifications for the authenticated student
    @GetMapping
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<Page<Notification>> getAllNotifications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long studentId = jwtUtil.extractUserId(token);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Notification> notifications = notificationService.getNotificationsByStudentId(studentId, pageable);
        return ResponseEntity.ok(notifications);
    }

    // Search notifications by internship title
    @GetMapping("/search")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<Page<Notification>> searchNotifications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long studentId = jwtUtil.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationService.searchByInternshipTitle(
                studentId, query, pageable);
        return ResponseEntity.ok(notifications);
    }

    // Get a specific notification
    @GetMapping("/{id}")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<Notification> getNotification(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long studentId = jwtUtil.extractUserId(token);

        return notificationService.getNotificationByIdAndStudentId(id, studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mark notification as read
    @PatchMapping("/{id}/read")
    @JwtValidation(requiredRoles = {"student"})
    public ResponseEntity<Notification> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long studentId = jwtUtil.extractUserId(token);

        Notification notification = notificationService.markAsRead(id, studentId);
        return ResponseEntity.ok(notification);
    }
    @DeleteMapping("/bulk")
    @JwtValidation(requiredRoles = {"admin", "hr"})
    public ResponseEntity<Void> deleteNotificationsByApplicationIds(@RequestBody List<Long> applicationIds) {
        notificationService.deleteNotificationsByApplicationIds(applicationIds);
        return ResponseEntity.noContent().build();
    }
}