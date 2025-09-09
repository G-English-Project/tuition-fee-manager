package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.NotificationDeleteEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.NotificationStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy tất cả thông báo của một user theo thứ tự mới nhất
    Page<Notification> findByUserOrderBySentAtDesc(User user, Pageable pageable);

    // Lấy tất cả thông báo chưa bị delete của một user theo thứ tự mới nhất
    Page<Notification> findByUserAndDeleteOrderBySentAtDesc(User user, NotificationDeleteEnum deleteStatus, Pageable pageable);

    // Lấy toàn bộ thông báo chưa đọc của user
    List<Notification> findByUserAndStatus(User user, NotificationStatusEnum status);

    // Đếm số lượng chưa đọc
    long countByUserAndStatus(User user, NotificationStatusEnum status);

    // Lấy toàn bộ thông báo theo delete status của user
    List<Notification> findByUserAndDelete(User user, NotificationDeleteEnum deleteStatus);

    // Lấy thông báo theo user, status và delete status
    List<Notification> findByUserAndStatusAndDelete(User user, NotificationStatusEnum status, NotificationDeleteEnum deleteStatus);
}
