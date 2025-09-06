package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Feedback.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.SupportLevelEnum;
import lombok.Data;

@Data
public class FeedbackRequestDto {
    private Long studentId;
    private Long teacherId;
    private Integer professionalLevel;
    private Integer materialSuitability;
    private SupportLevelEnum supportLevel;
    private String extraHelpNeeded;
    private Integer overallExperience;
}