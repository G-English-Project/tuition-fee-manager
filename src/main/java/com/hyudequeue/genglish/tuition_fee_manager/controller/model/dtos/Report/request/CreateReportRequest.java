package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.AttendanceEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.HomeworkEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ParticipationEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequest {
    @NotNull private Long studentId;
    @NotNull private Long classId;
    @NotNull private Long teacherId;

    @NotNull private AttendanceEnum attendance;
    @NotNull private HomeworkEnum homework;
    @NotNull private ParticipationEnum participation;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer skillProgress;

    @NotBlank private String areasForImprovement;
    @NotBlank private String recommendedAction;

    private String imageThumbBase64;
    private String imageBase64;
    private String mimeType;
}
