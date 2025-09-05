package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponseDTO {
    private Long reportId;

    private String title;
    private String content;

    private Boolean hasImage;

    private Long studentId;
    private String studentName;

    private Long classId;
    private String className;

    private Double point;

    private LocalDateTime createdAt;
}