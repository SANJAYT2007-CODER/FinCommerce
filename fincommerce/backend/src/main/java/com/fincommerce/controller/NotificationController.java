package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.entity.Notification;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Notification> list = notificationService.getUserNotifications(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", list));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long count = notificationService.getUnreadCount(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Unread notification count retrieved", count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        notificationService.deleteNotification(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"));
    }
}


