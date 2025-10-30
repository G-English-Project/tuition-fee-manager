package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.AttendanceEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.HomeworkEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ParticipationEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponseDTO {
    private Long reportId;

    private Boolean hasImage;

    private Long studentId;
    private String studentName;

    private Long classId;
    private String className;

    private Long teacherId;
    private String teacherName;

    private AttendanceEnum attendance;
    private HomeworkEnum homework;
    private ParticipationEnum participation;
    private Integer skillProgress;
    private String areasForImprovement;
    private String recommendedAction;

    private LocalDateTime createdAt;
}