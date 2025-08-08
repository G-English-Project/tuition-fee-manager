package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Notification.response.NotificationResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto createNotification(Long userId, String subject, String body);

    void markAsRead(Long notificationId);

    void markAsRead(List<Long> notificationIds);

    void markAllAsRead(Long userId);

    Page<NotificationResponseDto> getNotifications(Long userId, Pageable pageable);

    long countUnreadNotifications(Long userId);
}
