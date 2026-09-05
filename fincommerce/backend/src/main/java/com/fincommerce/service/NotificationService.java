package com.fincommerce.service;

import com.fincommerce.entity.Notification;
import com.fincommerce.entity.User;
import com.fincommerce.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, String type, String linkUrl) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLinkUrl(linkUrl);
        n.setIsRead(false);
        return notificationRepository.save(n);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).size();
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getId().equals(userId)) {
                notificationRepository.delete(n);
            }
        });
    }
}


