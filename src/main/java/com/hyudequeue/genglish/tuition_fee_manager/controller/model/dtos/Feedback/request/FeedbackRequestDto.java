package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request;


import lombok.Data;

@Data
public class FeedbackRequestDto {
    private Long classId;
    private Long studentId;
    private String content;
    private Integer rating;
}
