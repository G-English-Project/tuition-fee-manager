package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.response;


import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.SupportLevelEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDto {
    private Long feedbackId;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Integer professionalLevel;
    private Integer materialSuitability;
    private SupportLevelEnum supportLevel;
    private String extraHelpNeeded;
    private Integer overallExperience;
    private LocalDateTime createdAt;
}
