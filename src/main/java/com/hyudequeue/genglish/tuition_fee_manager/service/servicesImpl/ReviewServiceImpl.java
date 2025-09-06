package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;


import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.request.ReviewRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Review.response.ReviewResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Review;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;

import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ReviewRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;


    @Override
    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        Classes classes = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Review review = Review.builder()
                .classes(classes)
                .student(student)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();

        Review saved = reviewRepository.save(review);


        // 🔔 Notify admin khi có feedback mới
        invoiceNotificationService.notifyAdminNewFeedback(
                student,
                classes,
                saved.getContent()
        );
        return toDto(saved);
    }

    @Override
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDto> getReviewsByStudent(Long studentId) {
        return reviewRepository.findByStudent_UserId(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // nếu cho phép update class/student thì giữ lại, còn không thì chỉ update content + rating
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        Review updated = reviewRepository.save(review);
        return toDto(updated);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        reviewRepository.delete(review);
    }


    private ReviewResponseDto toDto(Review f) {
        return ReviewResponseDto.builder()
                .reviewId(f.getReviewId())
                .className(f.getClasses().getClassName())
                .studentName(f.getStudent().getFullName())
                .content(f.getContent())
                .rating(f.getRating())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
