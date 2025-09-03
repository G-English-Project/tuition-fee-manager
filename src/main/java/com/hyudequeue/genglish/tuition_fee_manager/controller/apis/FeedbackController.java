package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.FeedbackEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(FeedbackEndpoints.BASE)
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
}