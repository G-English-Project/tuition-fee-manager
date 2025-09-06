package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ReviewEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.request.ReviewRequestDto;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.response.ReviewResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ReviewService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.REVIEW_API) // "/api/v1/feedback"
@RequiredArgsConstructor
public class ReivewController {

    private final ReviewService reviewService;

    // 1. Học sinh tạo feedback
    @PostMapping(ReviewEndpoints.CREATE_REVIEW)
    public ReviewResponseDto createReview(@RequestBody ReviewRequestDto dto) {
        return reviewService.createReview(dto);
    }

    // 2. Admin xem tất cả feedback
    @GetMapping(ReviewEndpoints.GET_ALL_REVIEWS)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // 3. Xem feedback theo học sinh
    @GetMapping(ReviewEndpoints.GET_REVIEWS_BY_STUDENT)
    public List<ReviewResponseDto> getReviewByStudent(@PathVariable Long studentId) {
        return reviewService.getReviewsByStudent(studentId);
    }
    // 4. Update review
    @PutMapping(ReviewEndpoints.UPDATE_REVIEW)
    public ReviewResponseDto updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto dto
    ) {
        return reviewService.updateReview(reviewId, dto);
    }

    // 5. Delete review
    @DeleteMapping(ReviewEndpoints.DELETE_REVIEW)
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

}