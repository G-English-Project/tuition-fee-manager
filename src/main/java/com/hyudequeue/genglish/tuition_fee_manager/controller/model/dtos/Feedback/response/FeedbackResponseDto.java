package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDto {
    private Long feedbackId;
    private String className;
    private String studentName;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}
