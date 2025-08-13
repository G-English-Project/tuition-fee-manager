package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder  @Getter
public class EnrolledClassLiteDto {
    private Long classId;
    private String className;
    private LocalDateTime enrolledAt;
}