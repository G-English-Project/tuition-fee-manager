package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithClassDto {
    private Long userId;
    private String email;
    private String fullName;
    private Long classId;
    private String className;
    private LocalDateTime createdAt;
}