package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.FeedbackEndpoints.*;

@RestController
@RequestMapping(BASE)
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping(CREATE_FEEDBACK)
    public FeedbackResponseDto createFeedback(@RequestBody FeedbackRequestDto dto) {
        return feedbackService.createFeedback(dto);
    }

    @GetMapping(GET_FEEDBACK_BY_ID)
    public FeedbackResponseDto getFeedbackById(@PathVariable Long feedbackId) {
        return feedbackService.getFeedbackById(feedbackId);
    }

    @GetMapping(GET_ALL_FEEDBACKS)
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    @PutMapping(UPDATE_FEEDBACK)
    public FeedbackResponseDto updateFeedback(@PathVariable Long feedbackId, @RequestBody FeedbackRequestDto dto) {
        return feedbackService.updateFeedback(feedbackId, dto);
    }

    @DeleteMapping(DELETE_FEEDBACK)
    public void deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
    }

    @GetMapping(GET_FEEDBACKS_BY_STUDENT)
    public List<FeedbackResponseDto> getFeedbacksByStudentId(@PathVariable Long studentId) {
        return feedbackService.getFeedbacksByStudentId(studentId);
    }

    @GetMapping(GET_FEEDBACKS_BY_TEACHER)
    public List<FeedbackResponseDto> getFeedbacksByTeacherId(@PathVariable Long teacherId) {
        return feedbackService.getFeedbacksByTeacherId(teacherId);
    }

    @GetMapping(GET_GOOD_FEEDBACKS)
    public Page<FeedbackResponseDto> getGoodFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return feedbackService.getGoodFeedbacks(pageable);
    }

    @GetMapping("/search")
    public Page<FeedbackResponseDto> searchFeedbacks(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer overallRate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return feedbackService.searchFeedbacks(
                teacherId, classId, overallRate, fromDate, toDate, pageable
        );
    }

}