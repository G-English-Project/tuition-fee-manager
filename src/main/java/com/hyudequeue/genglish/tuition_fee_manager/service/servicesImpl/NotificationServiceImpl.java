package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Notification.response.NotificationResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.NotificationStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.NotificationRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(Long userId, String subject, String body) {
        User user = entityManager.getReference(User.class, userId);;
        Notification notification = Notification.builder()
                .user(user)
                .subject(subject)
                .body(body)
                .sentAt(LocalDateTime.now())
                .status(NotificationStatusEnum.UNREAD)
                .build();
        return NotificationResponseDto.ToDto(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void createNotifications(List<Long> userIds, String subject, String body) {
        if (userIds == null || userIds.isEmpty()) return;

        // Tránh N+1: dùng getReferenceById, không cần load full User
        List<Notification> list = new ArrayList<>(userIds.size());
        LocalDateTime now = LocalDateTime.now();

        for (Long uid : userIds) {
            User ref = entityManager.getReference(User.class, uid);
            list.add(Notification.builder()
                    .user(ref)
                    .subject(subject)
                    .body(body)
                    .sentAt(now)
                    .status(NotificationStatusEnum.UNREAD)
                    .build());
        }
        notificationRepository.saveAll(list);
    }

    @Override
    @Transactional
    public void createNotifications(Map<Long, String> perUserBodies, String subject) {
        if (perUserBodies == null || perUserBodies.isEmpty()) return;

        List<Notification> list = new ArrayList<>(perUserBodies.size());
        LocalDateTime now = LocalDateTime.now();

        perUserBodies.forEach((uid, body) -> {
            User ref = entityManager.getReference(User.class, uid);
            list.add(Notification.builder()
                    .user(ref)
                    .subject(subject)
                    .body(body)
                    .sentAt(now)
                    .status(NotificationStatusEnum.UNREAD)
                    .build());
        });

        notificationRepository.saveAll(list);
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
    public Page<NotificationResponseDto> getNotifications(Long userId, Pageable pageable) {
        User user = getUserOrThrow(userId);
        Page<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user, pageable);
        return notifications.map(NotificationResponseDto::ToDto);
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
