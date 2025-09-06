package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private String className;
    private String studentName;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}
