package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedbackService {
    FeedbackResponseDto createFeedback(FeedbackRequestDto dto);
    FeedbackResponseDto getFeedbackById(Long id);
    List<FeedbackResponseDto> getAllFeedbacks();
    FeedbackResponseDto updateFeedback(Long id, FeedbackRequestDto dto);
    void deleteFeedback(Long id);
    List<FeedbackResponseDto> getFeedbacksByStudentId(Long studentId);
    List<FeedbackResponseDto> getFeedbacksByTeacherId(Long teacherId);
    Page<FeedbackResponseDto> getGoodFeedbacks(Pageable pageable);
}