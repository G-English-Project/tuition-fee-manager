package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;


import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Feedback;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;

import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.FeedbackRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.FeedbackService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;

    @Override
    public FeedbackResponseDto createFeedback(FeedbackRequestDto dto) {
        Classes classes = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Feedback feedback = Feedback.builder()
                .classes(classes)
                .student(student)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();

        Feedback saved = feedbackRepository.save(feedback);


        // 🔔 Notify admin khi có feedback mới
        invoiceNotificationService.notifyAdminNewFeedback(
                student,
                classes,
                saved.getContent()
        );
        return toDto(saved);
    }

    @Override
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponseDto> getFeedbacksByStudent(Long studentId) {
        return feedbackRepository.findByStudent_UserId(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private FeedbackResponseDto toDto(Feedback f) {
        return FeedbackResponseDto.builder()
                .feedbackId(f.getFeedbackId())
                .className(f.getClasses().getClassName())
                .studentName(f.getStudent().getFullName())
                .content(f.getContent())
                .rating(f.getRating())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
