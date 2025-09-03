package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequest {
    @NotNull private Long studentId;
    @NotNull private Long classId;

    @NotBlank private String title;
    @NotBlank private String content;

    private String imageThumbBase64;
    private String imageBase64;
    private String mimeType;
}
