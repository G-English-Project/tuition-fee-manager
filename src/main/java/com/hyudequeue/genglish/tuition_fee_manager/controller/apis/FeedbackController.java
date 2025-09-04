package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.FeedbackEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.FeedbackService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.FEEDBACK_API) // "/api/v1/feedback"
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // 1. Học sinh tạo feedback
    @PostMapping(FeedbackEndpoints.CREATE_FEEDBACK)
    public FeedbackResponseDto createFeedback(@RequestBody FeedbackRequestDto dto) {
        return feedbackService.createFeedback(dto);
    }

    // 2. Admin xem tất cả feedback
    @GetMapping(FeedbackEndpoints.GET_ALL_FEEDBACKS)
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    // 3. Xem feedback theo học sinh
    @GetMapping(FeedbackEndpoints.GET_FEEDBACKS_BY_STUDENT)
    public List<FeedbackResponseDto> getFeedbacksByStudent(@PathVariable Long studentId) {
        return feedbackService.getFeedbacksByStudent(studentId);
    }

    // 4. Update feedback
    @PutMapping(FeedbackEndpoints.UPDATE_FEEDBACK)
    public FeedbackResponseDto updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackRequestDto dto
    ) {
        return feedbackService.updateFeedback(feedbackId, dto);
    }

    // 5. Delete feedback
    @DeleteMapping(FeedbackEndpoints.DELETE_FEEDBACK)
    public void deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
    }
}