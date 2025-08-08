package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.NotificationStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.NotificationRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public Notification createNotification(Long userId, String subject, String body) {
        User user = getUserOrThrow(userId);
        Notification notification = Notification.builder()
                .user(user)
                .subject(subject)
                .body(body)
                .sentAt(LocalDateTime.now())
                .status(NotificationStatusEnum.UNREAD)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatusEnum.READ);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void markAsRead(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        notifications.forEach(n -> n.setStatus(NotificationStatusEnum.READ));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAllAsRead(Long userId) {
        User user = getUserOrThrow(userId);
        List<Notification> unread = notificationRepository.findByUserAndStatus(user, NotificationStatusEnum.UNREAD);
        unread.forEach(n -> n.setStatus(NotificationStatusEnum.READ));
        notificationRepository.saveAll(unread);
    }

    @Override
    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        User user = getUserOrThrow(userId);
        return notificationRepository.findByUserOrderBySentAtDesc(user, pageable);
    }

    @Override
    public long countUnreadNotifications(Long userId) {
        User user = getUserOrThrow(userId);
        return notificationRepository.countByUserAndStatus(user, NotificationStatusEnum.UNREAD);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"User not found with id: " + userId));
    }
}
