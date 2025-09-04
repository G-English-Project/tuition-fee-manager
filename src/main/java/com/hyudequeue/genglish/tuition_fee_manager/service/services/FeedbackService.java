package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;

import java.util.List;

public interface FeedbackService {
    FeedbackResponseDto createFeedback(FeedbackRequestDto dto);
    List<FeedbackResponseDto> getAllFeedbacks();
    List<FeedbackResponseDto> getFeedbacksByStudent(Long studentId);
    FeedbackResponseDto updateFeedback(Long feedbackId, FeedbackRequestDto dto);
    void deleteFeedback(Long feedbackId);
}
