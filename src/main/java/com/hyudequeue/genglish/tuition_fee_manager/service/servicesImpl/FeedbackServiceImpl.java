package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request.FeedbackRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response.FeedbackResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Feedback;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.FeedbackRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    private FeedbackResponseDto mapToDto(Feedback feedback) {
        return FeedbackResponseDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .studentId(feedback.getStudent().getUserId())
                .studentName(feedback.getStudent().getFullName())
                .teacherId(feedback.getTeacher().getUserId())
                .teacherName(feedback.getTeacher().getFullName())
                .professionalLevel(feedback.getProfessionalLevel())
                .materialSuitability(feedback.getMaterialSuitability())
                .supportLevel(feedback.getSupportLevel())
                .extraHelpNeeded(feedback.getExtraHelpNeeded())
                .overallExperience(feedback.getOverallExperience())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    @Override
    public FeedbackResponseDto createFeedback(FeedbackRequestDto dto) {
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Feedback feedback = Feedback.builder()
                .student(student)
                .teacher(teacher)
                .professionalLevel(dto.getProfessionalLevel())
                .materialSuitability(dto.getMaterialSuitability())
                .supportLevel(dto.getSupportLevel())
                .extraHelpNeeded(dto.getExtraHelpNeeded())
                .overallExperience(dto.getOverallExperience())
                .build();

        return mapToDto(feedbackRepository.save(feedback));
    }

    @Override
    public FeedbackResponseDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        return mapToDto(feedback);
    }

    @Override
    public List<FeedbackResponseDto> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponseDto updateFeedback(Long id, FeedbackRequestDto dto) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        feedback.setStudent(student);
        feedback.setTeacher(teacher);
        feedback.setProfessionalLevel(dto.getProfessionalLevel());
        feedback.setMaterialSuitability(dto.getMaterialSuitability());
        feedback.setSupportLevel(dto.getSupportLevel());
        feedback.setExtraHelpNeeded(dto.getExtraHelpNeeded());
        feedback.setOverallExperience(dto.getOverallExperience());

        return mapToDto(feedbackRepository.save(feedback));
    }

    @Override
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<FeedbackResponseDto> getFeedbacksByStudentId(Long studentId) {
        return feedbackRepository.findByStudentUserId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponseDto> getFeedbacksByTeacherId(Long teacherId) {
        return feedbackRepository.findByTeacherUserId(teacherId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}