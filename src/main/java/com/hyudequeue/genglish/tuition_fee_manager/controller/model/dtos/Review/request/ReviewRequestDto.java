package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.request;


import lombok.Data;

@Data
public class ReviewRequestDto {
    private Long classId;
    private Long studentId;
    private String content;
    private Integer rating;
}
