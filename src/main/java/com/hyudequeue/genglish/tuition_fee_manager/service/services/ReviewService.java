package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.request.ReviewRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(ReviewRequestDto dto);
    List<ReviewResponseDto> getAllReviews();
    List<ReviewResponseDto> getReviewsByStudent(Long studentId);
    ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto);
    void deleteReview(Long reviewId);
}
