package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.AttendanceEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.HomeworkEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ParticipationEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateReportRequest {
    private AttendanceEnum attendance;
    private HomeworkEnum homework;
    private ParticipationEnum participation;

    @Min(1)
    @Max(5)
    private Integer skillProgress;

    private String areasForImprovement;
    private String recommendedAction;

    private String imageThumbBase64;
    private String imageBase64;
    private String mimeType;
}