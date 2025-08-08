package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Long userId, String subject, String body);

    void markAsRead(Long notificationId);

    void markAsRead(List<Long> notificationIds);

    void markAllAsRead(Long userId);

    Page<Notification> getNotifications(Long userId, Pageable pageable);

    long countUnreadNotifications(Long userId);
}
