package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Notification.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.NotificationStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Notification;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long notificationId;
    private Long userId;
    private String subject;
    private String body;
    private LocalDateTime sentAt;
    private NotificationStatusEnum status;

    public static NotificationResponseDto ToDto(Notification notification){
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .sentAt(notification.getSentAt())
                .status(notification.getStatus())
                .build();
    }
}

